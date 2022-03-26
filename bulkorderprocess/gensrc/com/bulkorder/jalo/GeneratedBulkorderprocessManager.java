/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at Mar 26, 2022, 8:53:57 AM                    ---
 * ----------------------------------------------------------------
 */
package com.bulkorder.jalo;

import com.bulkorder.constants.BulkorderprocessConstants;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jalo.order.CartEntry;
import de.hybris.platform.ordersplitting.jalo.ConsignmentProcess;
import de.hybris.platform.processengine.jalo.BusinessProcess;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type <code>BulkorderprocessManager</code>.
 */
@SuppressWarnings({"deprecation","unused","cast"})
public abstract class GeneratedBulkorderprocessManager extends Extension
{
	protected static final Map<String, Map<String, AttributeMode>> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, Map<String, AttributeMode>> ttmp = new HashMap();
		Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put("done", AttributeMode.INITIAL);
		tmp.put("waitingForConsignment", AttributeMode.INITIAL);
		tmp.put("warehouseConsignmentState", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.ordersplitting.jalo.ConsignmentProcess", Collections.unmodifiableMap(tmp));
		tmp = new HashMap<String, AttributeMode>();
		tmp.put("addressFile", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.jalo.order.Cart", Collections.unmodifiableMap(tmp));
		tmp = new HashMap<String, AttributeMode>();
		tmp.put("addressFile", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.jalo.order.CartEntry", Collections.unmodifiableMap(tmp));
		DEFAULT_INITIAL_ATTRIBUTES = ttmp;
	}
	@Override
	public Map<String, AttributeMode> getDefaultAttributeModes(final Class<? extends Item> itemClass)
	{
		Map<String, AttributeMode> ret = new HashMap<>();
		final Map<String, AttributeMode> attr = DEFAULT_INITIAL_ATTRIBUTES.get(itemClass.getName());
		if (attr != null)
		{
			ret.putAll(attr);
		}
		return ret;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Cart.addressFile</code> attribute.
	 * @return the addressFile - type of cart either bulk or normal
	 */
	public Media getAddressFile(final SessionContext ctx, final Cart item)
	{
		return (Media)item.getProperty( ctx, BulkorderprocessConstants.Attributes.Cart.ADDRESSFILE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Cart.addressFile</code> attribute.
	 * @return the addressFile - type of cart either bulk or normal
	 */
	public Media getAddressFile(final Cart item)
	{
		return getAddressFile( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Cart.addressFile</code> attribute. 
	 * @param value the addressFile - type of cart either bulk or normal
	 */
	public void setAddressFile(final SessionContext ctx, final Cart item, final Media value)
	{
		item.setProperty(ctx, BulkorderprocessConstants.Attributes.Cart.ADDRESSFILE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Cart.addressFile</code> attribute. 
	 * @param value the addressFile - type of cart either bulk or normal
	 */
	public void setAddressFile(final Cart item, final Media value)
	{
		setAddressFile( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>CartEntry.addressFile</code> attribute.
	 * @return the addressFile - type of cart either bulk or normal
	 */
	public Media getAddressFile(final SessionContext ctx, final CartEntry item)
	{
		return (Media)item.getProperty( ctx, BulkorderprocessConstants.Attributes.CartEntry.ADDRESSFILE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>CartEntry.addressFile</code> attribute.
	 * @return the addressFile - type of cart either bulk or normal
	 */
	public Media getAddressFile(final CartEntry item)
	{
		return getAddressFile( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>CartEntry.addressFile</code> attribute. 
	 * @param value the addressFile - type of cart either bulk or normal
	 */
	public void setAddressFile(final SessionContext ctx, final CartEntry item, final Media value)
	{
		item.setProperty(ctx, BulkorderprocessConstants.Attributes.CartEntry.ADDRESSFILE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>CartEntry.addressFile</code> attribute. 
	 * @param value the addressFile - type of cart either bulk or normal
	 */
	public void setAddressFile(final CartEntry item, final Media value)
	{
		setAddressFile( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentProcess.done</code> attribute.
	 * @return the done - Mark process as done
	 */
	public Boolean isDone(final SessionContext ctx, final ConsignmentProcess item)
	{
		return (Boolean)item.getProperty( ctx, BulkorderprocessConstants.Attributes.ConsignmentProcess.DONE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentProcess.done</code> attribute.
	 * @return the done - Mark process as done
	 */
	public Boolean isDone(final ConsignmentProcess item)
	{
		return isDone( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentProcess.done</code> attribute. 
	 * @return the done - Mark process as done
	 */
	public boolean isDoneAsPrimitive(final SessionContext ctx, final ConsignmentProcess item)
	{
		Boolean value = isDone( ctx,item );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentProcess.done</code> attribute. 
	 * @return the done - Mark process as done
	 */
	public boolean isDoneAsPrimitive(final ConsignmentProcess item)
	{
		return isDoneAsPrimitive( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ConsignmentProcess.done</code> attribute. 
	 * @param value the done - Mark process as done
	 */
	public void setDone(final SessionContext ctx, final ConsignmentProcess item, final Boolean value)
	{
		item.setProperty(ctx, BulkorderprocessConstants.Attributes.ConsignmentProcess.DONE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ConsignmentProcess.done</code> attribute. 
	 * @param value the done - Mark process as done
	 */
	public void setDone(final ConsignmentProcess item, final Boolean value)
	{
		setDone( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ConsignmentProcess.done</code> attribute. 
	 * @param value the done - Mark process as done
	 */
	public void setDone(final SessionContext ctx, final ConsignmentProcess item, final boolean value)
	{
		setDone( ctx, item, Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ConsignmentProcess.done</code> attribute. 
	 * @param value the done - Mark process as done
	 */
	public void setDone(final ConsignmentProcess item, final boolean value)
	{
		setDone( getSession().getSessionContext(), item, value );
	}
	
	@Override
	public String getName()
	{
		return BulkorderprocessConstants.EXTENSIONNAME;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentProcess.waitingForConsignment</code> attribute.
	 * @return the waitingForConsignment - Mark that process is waiting for consignment
	 */
	public Boolean isWaitingForConsignment(final SessionContext ctx, final ConsignmentProcess item)
	{
		return (Boolean)item.getProperty( ctx, BulkorderprocessConstants.Attributes.ConsignmentProcess.WAITINGFORCONSIGNMENT);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentProcess.waitingForConsignment</code> attribute.
	 * @return the waitingForConsignment - Mark that process is waiting for consignment
	 */
	public Boolean isWaitingForConsignment(final ConsignmentProcess item)
	{
		return isWaitingForConsignment( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentProcess.waitingForConsignment</code> attribute. 
	 * @return the waitingForConsignment - Mark that process is waiting for consignment
	 */
	public boolean isWaitingForConsignmentAsPrimitive(final SessionContext ctx, final ConsignmentProcess item)
	{
		Boolean value = isWaitingForConsignment( ctx,item );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentProcess.waitingForConsignment</code> attribute. 
	 * @return the waitingForConsignment - Mark that process is waiting for consignment
	 */
	public boolean isWaitingForConsignmentAsPrimitive(final ConsignmentProcess item)
	{
		return isWaitingForConsignmentAsPrimitive( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ConsignmentProcess.waitingForConsignment</code> attribute. 
	 * @param value the waitingForConsignment - Mark that process is waiting for consignment
	 */
	public void setWaitingForConsignment(final SessionContext ctx, final ConsignmentProcess item, final Boolean value)
	{
		item.setProperty(ctx, BulkorderprocessConstants.Attributes.ConsignmentProcess.WAITINGFORCONSIGNMENT,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ConsignmentProcess.waitingForConsignment</code> attribute. 
	 * @param value the waitingForConsignment - Mark that process is waiting for consignment
	 */
	public void setWaitingForConsignment(final ConsignmentProcess item, final Boolean value)
	{
		setWaitingForConsignment( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ConsignmentProcess.waitingForConsignment</code> attribute. 
	 * @param value the waitingForConsignment - Mark that process is waiting for consignment
	 */
	public void setWaitingForConsignment(final SessionContext ctx, final ConsignmentProcess item, final boolean value)
	{
		setWaitingForConsignment( ctx, item, Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ConsignmentProcess.waitingForConsignment</code> attribute. 
	 * @param value the waitingForConsignment - Mark that process is waiting for consignment
	 */
	public void setWaitingForConsignment(final ConsignmentProcess item, final boolean value)
	{
		setWaitingForConsignment( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentProcess.warehouseConsignmentState</code> attribute.
	 * @return the warehouseConsignmentState - State of warehouse process.
	 */
	public EnumerationValue getWarehouseConsignmentState(final SessionContext ctx, final ConsignmentProcess item)
	{
		return (EnumerationValue)item.getProperty( ctx, BulkorderprocessConstants.Attributes.ConsignmentProcess.WAREHOUSECONSIGNMENTSTATE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentProcess.warehouseConsignmentState</code> attribute.
	 * @return the warehouseConsignmentState - State of warehouse process.
	 */
	public EnumerationValue getWarehouseConsignmentState(final ConsignmentProcess item)
	{
		return getWarehouseConsignmentState( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ConsignmentProcess.warehouseConsignmentState</code> attribute. 
	 * @param value the warehouseConsignmentState - State of warehouse process.
	 */
	public void setWarehouseConsignmentState(final SessionContext ctx, final ConsignmentProcess item, final EnumerationValue value)
	{
		item.setProperty(ctx, BulkorderprocessConstants.Attributes.ConsignmentProcess.WAREHOUSECONSIGNMENTSTATE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ConsignmentProcess.warehouseConsignmentState</code> attribute. 
	 * @param value the warehouseConsignmentState - State of warehouse process.
	 */
	public void setWarehouseConsignmentState(final ConsignmentProcess item, final EnumerationValue value)
	{
		setWarehouseConsignmentState( getSession().getSessionContext(), item, value );
	}
	
}
