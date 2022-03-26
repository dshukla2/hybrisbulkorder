package com.bulkorder.strategy.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.externaltax.ExternalTaxesService;
import de.hybris.platform.commerceservices.order.CommercePlaceOrderStrategy;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class CreateBulkOrderFromCartStrategy implements CommercePlaceOrderStrategy
{
	private static final Logger LOG = Logger.getLogger(CreateBulkOrderFromCartStrategy.class);
	private KeyGenerator keyGenerator;
	private CartService cartService;
	private TypeService typeService;
	private ModelService modelService;
	private CommonI18NService commonI18NService;
	private OrderService orderService;
	private BaseSiteService baseSiteService;
	private BaseStoreService baseStoreService;
	private PromotionsService promotionsService;
	private CalculationService calculationService;
	private ExternalTaxesService externalTaxesService;
	private List<CommercePlaceOrderMethodHook> commercePlaceOrderMethodHooks;
	private TimeService timeService;
	private MediaService mediaService;
	private ConfigurationService configurationService;

	@Override
	public CommerceOrderResult placeOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		final CartModel cartModel = parameter.getCart();
		validateParameterNotNull(cartModel, "Cart model cannot be null");
		final CommerceOrderResult result = new CommerceOrderResult();
		try
		{
			beforePlaceOrder(parameter);
			if (calculationService.requiresCalculation(cartModel))
			{
				// does not make sense to fail here especially since we don't fail below when we calculate order.
				// throw new IllegalArgumentException(String.format("Cart [%s] must be calculated", cartModel.getCode()));
				LOG.error(String.format("CartModel's [%s] calculated flag was false", cartModel.getCode()));
			}

			final CustomerModel customer = (CustomerModel) cartModel.getUser();
			validateParameterNotNull(customer, "Customer model cannot be null");
			if (null != cartModel.getAddressFile())
			{
				placeOrdersForBulkOrder(parameter, cartModel, result, customer);
			}
			final OrderModel orderModel = getOrderService().createOrderFromCart(cartModel);
			populateAndSubmitOrder(parameter, cartModel, result, customer, orderModel);
		}
		finally
		{
			getExternalTaxesService().clearSessionTaxDocument();
		}

		this.afterPlaceOrder(parameter, result);
		return result;

	}

	/**
	 * @param parameter
	 * @param cartModel
	 * @param result
	 * @param customer
	 * @param orderModel
	 * @throws InvalidCartException
	 */
	private void populateAndSubmitOrder(final CommerceCheckoutParameter parameter, final CartModel cartModel,
			final CommerceOrderResult result, final CustomerModel customer, final OrderModel orderModel) throws InvalidCartException
	{
		if (orderModel != null)
		{
			// Reset the Date attribute for use in determining when the order was placed
			orderModel.setDate(getTimeService().getCurrentTime());



			// Store the current site and store on the order
			orderModel.setSite(getBaseSiteService().getCurrentBaseSite());
			orderModel.setStore(getBaseStoreService().getCurrentBaseStore());
			orderModel.setLanguage(getCommonI18NService().getCurrentLanguage());

			if (parameter.getSalesApplication() != null)
			{
				orderModel.setSalesApplication(parameter.getSalesApplication());
			}

			// clear the promotionResults that where cloned from cart PromotionService.transferPromotionsToOrder will copy them over bellow.
			orderModel.setAllPromotionResults(Collections.<PromotionResultModel> emptySet());

			getModelService().saveAll(customer, orderModel);

			if (cartModel.getPaymentInfo() != null && cartModel.getPaymentInfo().getBillingAddress() != null)
			{
				final AddressModel billingAddress = cartModel.getPaymentInfo().getBillingAddress();
				orderModel.setPaymentAddress(billingAddress);
				orderModel.getPaymentInfo().setBillingAddress(getModelService().clone(billingAddress));
				getModelService().save(orderModel.getPaymentInfo());
			}
			getModelService().save(orderModel);
			// Transfer promotions to the order
			getPromotionsService().transferPromotionsToOrder(cartModel, orderModel, false);

			// Calculate the order now that it has been copied
			try
			{
				getCalculationService().calculateTotals(orderModel, false);
				getExternalTaxesService().calculateExternalTaxes(orderModel);
			}
			catch (final CalculationException ex)
			{
				LOG.error("Failed to calculate order [" + orderModel + "]", ex);
			}

			getModelService().refresh(orderModel);
			getModelService().refresh(customer);

			result.setOrder(orderModel);

			this.beforeSubmitOrder(parameter, result);

			getOrderService().submitOrder(orderModel);
		}
		else
		{
			throw new IllegalArgumentException(String.format("Order was not properly created from cart %s", cartModel.getCode()));
		}
	}

	protected void beforeSubmitOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		if (getCommercePlaceOrderMethodHooks() != null && (parameter.isEnableHooks()
				&& getConfigurationService().getConfiguration().getBoolean(CommerceServicesConstants.PLACEORDERHOOK_ENABLED, true)))
		{
			for (final CommercePlaceOrderMethodHook commercePlaceOrderMethodHook : getCommercePlaceOrderMethodHooks())
			{
				commercePlaceOrderMethodHook.beforeSubmitOrder(parameter, result);
			}
		}
	}

	protected void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		if (getCommercePlaceOrderMethodHooks() != null && (parameter.isEnableHooks()
				&& getConfigurationService().getConfiguration().getBoolean(CommerceServicesConstants.PLACEORDERHOOK_ENABLED, true)))
		{
			for (final CommercePlaceOrderMethodHook commercePlaceOrderMethodHook : getCommercePlaceOrderMethodHooks())
			{
				commercePlaceOrderMethodHook.afterPlaceOrder(parameter, result);
			}
		}
	}

	protected void beforePlaceOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		if (getCommercePlaceOrderMethodHooks() != null && parameter.isEnableHooks())
		{
			for (final CommercePlaceOrderMethodHook commercePlaceOrderMethodHook : getCommercePlaceOrderMethodHooks())
			{
				commercePlaceOrderMethodHook.beforePlaceOrder(parameter);
			}
		}
	}



	/**
	 * @param parameter
	 * @param cart
	 * @param customer
	 * @param result
	 */
	private void placeOrdersForBulkOrder(final CommerceCheckoutParameter parameter, final CartModel cart,
			final CommerceOrderResult result, final CustomerModel customer)
	{
		try
		{
			final MediaModel addressMedia = cart.getAddressFile();
			final List<AddressModel> listOfAddress = getAddressFromMedia(addressMedia);
			for (final AddressModel address : listOfAddress)
			{
				try
				{
					final CartModel individalCart = getCartForAddress(cart, address);
					final OrderModel individualOrder = getOrderService().createOrderFromCart(individalCart);
					populateAndSubmitOrder(parameter, cart, result, customer, individualOrder);
					LOG.info("Bulk Order | Individual Order created with Code " + individualOrder.getCode());
				}
				catch (final Exception e)
				{
					LOG.info("Bulk Order | Failed to place individual order for address pk : " + address.getPk(), e);
				}

			}
		}
		catch (final Exception e)
		{
			LOG.info("Error occured while plcing individula order for bulk cart", e);
		}
	}

	public CommerceOrderResult populateAndSubmitOrder(final CartModel individalCart, final OrderModel individualOrder)
	{
		final CommerceOrderResult result = new CommerceOrderResult();

		if (individualOrder != null)
		{
			// Reset the Date attribute for use in determining when the order was placed
			individualOrder.setDate(getTimeService().getCurrentTime());



			// Store the current site and store on the order
			individualOrder.setSite(getBaseSiteService().getCurrentBaseSite());
			individualOrder.setStore(getBaseStoreService().getCurrentBaseStore());
			individualOrder.setLanguage(getCommonI18NService().getCurrentLanguage());

			individualOrder.setSalesApplication(SalesApplication.WEB);


			// clear the promotionResults that where cloned from cart PromotionService.transferPromotionsToOrder will copy them over bellow.
			individualOrder.setAllPromotionResults(Collections.<PromotionResultModel> emptySet());

			getModelService().saveAll(individualOrder);

			if (individalCart.getPaymentInfo() != null && individalCart.getPaymentInfo().getBillingAddress() != null)
			{
				final AddressModel billingAddress = individalCart.getPaymentInfo().getBillingAddress();
				individualOrder.setPaymentAddress(billingAddress);
				individualOrder.getPaymentInfo().setBillingAddress(getModelService().clone(billingAddress));
				getModelService().save(individualOrder.getPaymentInfo());
			}
			getModelService().save(individualOrder);
			// Transfer promotions to the order
			getPromotionsService().transferPromotionsToOrder(individalCart, individualOrder, false);

			// Calculate the order now that it has been copied
			try
			{
				getCalculationService().calculateTotals(individualOrder, false);
				getExternalTaxesService().calculateExternalTaxes(individualOrder);
			}
			catch (final CalculationException ex)
			{
				LOG.error("Failed to calculate order [" + individualOrder + "]", ex);
			}

			getModelService().refresh(individualOrder);


			result.setOrder(individualOrder);


			getOrderService().submitOrder(individualOrder);
		}
		else
		{
			throw new IllegalArgumentException(
					String.format("Order was not properly created from cart %s", individalCart.getCode()));
		}

		return result;
	}



	/**
	 * @param cart
	 * @param address
	 * @return
	 */
	private CartModel getCartForAddress(final CartModel originalCart, final AddressModel address)
	{
		final CartModel individualCart = getCartService().clone(getTypeService().getComposedTypeForClass(CartModel.class),
				getTypeService().getComposedTypeForClass(CartEntryModel.class), originalCart,
				getKeyGenerator().generate().toString());
		individualCart.setPaymentAddress(originalCart.getPaymentAddress());
		individualCart.setDeliveryAddress(address);
		individualCart.setPaymentInfo(originalCart.getPaymentInfo());
		individualCart.setUser((UserModel) address.getOwner());
		return individualCart;
	}

	/**
	 * @param addressMedia
	 * @return
	 */

	private List<AddressModel> getAddressFromMedia(final MediaModel addressMedia)
	{
		final List<AddressModel> addressList = new ArrayList<AddressModel>();
		try
		{
			final byte[] addressByteStream = getMediaService().getDataFromMedia(addressMedia);
			final String text = new String(addressByteStream);
			final String[] lines = text.split("\\r?\\n");
			for (int i = 1; i < lines.length; i++)
			{
				final String[] record = lines[i].split(",");
				final AddressModel address = createAddressModel(record[0], record[1], record[2], record[3], record[4], record[5],
						record[6]);
				addressList.add(address);
			}
		}
		catch (final Exception e)
		{
			LOG.error("Error occured while fetching Sitemap pages from CMSsite : ", e);
		}
		return addressList;
	}


	private AddressModel createAddressModel(final String firstName, final String lastName, final String email,
			final String addLine1, final String addLine2, final String postalCode, final String town)
	{
		final AddressModel addressModel = (AddressModel) modelService.create(AddressModel.class);
		final UserModel user = (UserModel) modelService.create(UserModel.class);
		user.setUid(email);
		addressModel.setOwner(user);
		addressModel.setFirstname(firstName);
		addressModel.setLastname(lastName);
		addressModel.setEmail(email);
		addressModel.setLine1(addLine1);
		addressModel.setLine2(addLine2);
		addressModel.setPostalcode(postalCode);
		addressModel.setTown(town);
		return addressModel;
	}





	/**
	 * Generate a code for created order. Default implementation use {@link KeyGenerator}.
	 *
	 * @param cart
	 *           You can use a cart to generate new code for order.
	 */
	protected String generateOrderCode(final CartModel cart)
	{
		final Object generatedValue = keyGenerator.generate();
		if (generatedValue instanceof String)
		{
			return (String) generatedValue;
		}
		else
		{
			return String.valueOf(generatedValue);
		}
	}

	/**
	 * @return the baseSiteService
	 */
	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the promotionsService
	 */
	public PromotionsService getPromotionsService()
	{
		return promotionsService;
	}

	/**
	 * @param promotionsService
	 *           the promotionsService to set
	 */
	public void setPromotionsService(final PromotionsService promotionsService)
	{
		this.promotionsService = promotionsService;
	}

	/**
	 * @return the externalTaxesService
	 */
	public ExternalTaxesService getExternalTaxesService()
	{
		return externalTaxesService;
	}

	/**
	 * @param externalTaxesService
	 *           the externalTaxesService to set
	 */
	public void setExternalTaxesService(final ExternalTaxesService externalTaxesService)
	{
		this.externalTaxesService = externalTaxesService;
	}

	/**
	 * @return the orderService
	 */
	public OrderService getOrderService()
	{
		return orderService;
	}

	/**
	 * @param orderService
	 *           the orderService to set
	 */
	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;
	}

	/**
	 * @return the calculationService
	 */
	public CalculationService getCalculationService()
	{
		return calculationService;
	}

	/**
	 * @param calculationService
	 *           the calculationService to set
	 */
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}

	protected List<CommercePlaceOrderMethodHook> getCommercePlaceOrderMethodHooks()
	{
		return commercePlaceOrderMethodHooks;
	}

	public void setCommercePlaceOrderMethodHooks(final List<CommercePlaceOrderMethodHook> commercePlaceOrderMethodHooks)
	{
		this.commercePlaceOrderMethodHooks = commercePlaceOrderMethodHooks;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the keyGenerator
	 */
	public KeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	/**
	 * @param keyGenerator
	 *           the keyGenerator to set
	 */
	public void setKeyGenerator(final KeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the typeService
	 */
	public TypeService getTypeService()
	{
		return typeService;
	}

	/**
	 * @param typeService
	 *           the typeService to set
	 */
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	/**
	 * @return the timeService
	 */
	public TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * @return the mediaService
	 */
	public MediaService getMediaService()
	{
		return mediaService;
	}

	/**
	 * @param mediaService
	 *           the mediaService to set
	 */
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}





}
