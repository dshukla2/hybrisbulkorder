/**
 *
 */
package com.bulkorder.facade;

import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.dto.MediaFileDto;


/**
 * @author deeshukl3
 *
 */
public interface BulkMediaFacade
{

	MediaData addMedia(MediaData media, MediaFileDto mediaFile, final String cartCode, final String CartEntryCode);
}
