package com.bulkorder.strategy.impl;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.strategies.CartValidator;
import de.hybris.platform.order.strategies.CreateOrderFromCartStrategy;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.util.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class CreateBulkOrderFromCartStrategy implements CreateOrderFromCartStrategy
{
	private static final Logger LOG = Logger.getLogger(CreateBulkOrderFromCartStrategy.class);
	private CartValidator cartValidator;
	private CloneAbstractOrderStrategy cloneAbstractOrderStrategy;
	private KeyGenerator keyGenerator;
	private CartService cartService;
	private TypeService typeService;
	private ModelService modelService;

	@Override
	public OrderModel createOrderFromCart(final CartModel cart) throws InvalidCartException
	{
		if (cartValidator != null)
		{
			cartValidator.validateCart(cart);
		}

		placeOrdersForBulkOrder(cart);
		final OrderModel res = cloneAbstractOrderStrategy.clone(null, null, cart, generateOrderCode(cart), OrderModel.class,
				OrderEntryModel.class);


		return res;
	}

	/**
	 * @param cart
	 */
	private void placeOrdersForBulkOrder(final CartModel cart)
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
					final OrderModel individualOrder = cloneAbstractOrderStrategy.clone(null, null, individalCart,
							generateOrderCode(individalCart), OrderModel.class, OrderEntryModel.class);
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

	/**
	 * @param cart
	 * @param address
	 * @return
	 */
	private CartModel getCartForAddress(final CartModel originalCart, final AddressModel address)
	{
		final CartModel individualCart = cartService.clone(typeService.getComposedTypeForClass(CartModel.class),
				typeService.getComposedTypeForClass(CartEntryModel.class), originalCart, keyGenerator.generate().toString());
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
		List<AddressModel> addressList = new ArrayList<AddressModel>();
		try
		{
		final File file = MediaManager.getInstance().getMediaAsFile(addressMedia.getFolder().getQualifier(),
				addressMedia.getLocation());
		final CSVReader csvReader = new CSVReader(new FileReader(file));
		while (csvReader.readNextLine())
		{
			final Map<Integer, String> row = csvReader.getLine();
				addressList = getDummyAddressList();
			}
		}
		catch (final Exception e)
		{
			LOG.error("Error occured while reading file", e);
			addressList = getDummyAddressList();
		}

		return addressList;
	}


	private List<AddressModel> getDummyAddressList()
	{
		final List<AddressModel> addressList = new ArrayList<AddressModel>();
		final AddressModel addressModel1 = (AddressModel) modelService.create(AddressModel.class);
		final UserModel user1 = (UserModel) modelService.create(UserModel.class);
		addressModel1.setOwner(user1);
		addressModel1.setFirstname("firstname1");
		addressModel1.setLastname("lastname1");
		addressModel1.setEmail("asd@asd.com");
		addressModel1.setLine1("line1 line 1");
		addressModel1.setPostalcode("POSTAL");
		addressModel1.setTown("town");
		addressList.add(addressModel1);

		final AddressModel addressModel2 = (AddressModel) modelService.create(AddressModel.class);
		final UserModel user2 = (UserModel) modelService.create(UserModel.class);
		addressModel2.setOwner(user2);
		addressModel2.setFirstname("firstname1");
		addressModel2.setLastname("lastname1");
		addressModel2.setEmail("asd@asd.com");
		addressModel2.setLine1("line1 line 1");
		addressModel2.setPostalcode("POSTAL");
		addressModel2.setTown("town");
		addressList.add(addressModel2);

		return addressList;
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

	@Required
	public void setCartValidator(final CartValidator cartValidator)
	{
		this.cartValidator = cartValidator;
	}

	@Required
	public void setCloneAbstractOrderStrategy(final CloneAbstractOrderStrategy cloneAbstractOrderStrategy)
	{
		this.cloneAbstractOrderStrategy = cloneAbstractOrderStrategy;
	}

	@Required
	public void setKeyGenerator(final KeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}


	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

}
