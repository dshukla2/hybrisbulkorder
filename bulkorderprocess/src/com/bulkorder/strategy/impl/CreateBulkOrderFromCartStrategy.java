package com.bulkorder.strategy.impl;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.strategies.CartValidator;
import de.hybris.platform.order.strategies.CreateOrderFromCartStrategy;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class CreateBulkOrderFromCartStrategy implements CreateOrderFromCartStrategy
{

	private CartValidator cartValidator;
	private CloneAbstractOrderStrategy cloneAbstractOrderStrategy;
	private KeyGenerator keyGenerator;


	@Override
	public OrderModel createOrderFromCart(final CartModel cart) throws InvalidCartException
	{
		if (cartValidator != null)
		{
			cartValidator.validateCart(cart);
		}
		final MediaModel media = cart.getAddressFile();
		//read file to create cart.
		//List <addressModel> listOfAddress = getAddresses(media);
		//for(AddressModel address : listOfAddress){
		//
		//}
		//CartModel cart = (cart)

		final OrderModel res = cloneAbstractOrderStrategy.clone(null, null, cart, generateOrderCode(cart), OrderModel.class,
				OrderEntryModel.class);

		return res;
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

}
