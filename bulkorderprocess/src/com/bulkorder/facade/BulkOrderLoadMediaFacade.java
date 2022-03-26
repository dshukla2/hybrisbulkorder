/**
 *
 */
package com.bulkorder.facade;

import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.dto.MediaFileDto;
import de.hybris.platform.cmsfacades.media.impl.DefaultMediaFacade;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;




/**
 * @author deeshukl3
 *
 */
public class BulkOrderLoadMediaFacade extends DefaultMediaFacade implements BulkMediaFacade

{
	private CartService cartService;

	private final Logger LOG = Logger.getLogger(BulkOrderLoadMediaFacade.class);

	@Override
	public MediaData addMedia(final MediaData media, final MediaFileDto mediaFile, final String cartCode,
			final String CartEntryCode)
	{

		Preconditions.checkArgument(media != null);
		Preconditions.checkArgument(mediaFile != null);

		getFacadeValidationService().validate(getCreateMediaValidator(), media);
		getFacadeValidationService().validate(getCreateMediaFileValidator(), mediaFile);

		final MediaModel mediaModel = getModelService().create(MediaModel.class);
		getCreateMediaPopulator().populate(media, mediaModel);
		getCreateMediaFilePopulator().populate(mediaFile, mediaModel);
		getModelService().save(mediaModel);

		// can set a stream to an existing media model only
		populateStream(mediaFile, mediaModel);


		getModelService().save(mediaModel);
		final CartModel cart = getCartService().getSessionCart();

		if (StringUtils.isBlank(CartEntryCode))
		{
			cart.setAddressFile(mediaModel);
			getModelService().save(cart);
		}
		else
		{
			final CartEntryModel cartEntry = (CartEntryModel) cart.getEntries().get(Integer.parseInt(CartEntryCode));
			cartEntry.setAddressFile(mediaModel);
			getModelService().save(cartEntry);
		}
		return getMediaModelConverter().convert(mediaModel);
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}
}


