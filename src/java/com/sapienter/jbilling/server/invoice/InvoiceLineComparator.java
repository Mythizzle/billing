/*
 jBilling - The Enterprise Open Source Billing System
 Copyright (C) 2003-2011 Enterprise jBilling Software Ltd. and Emiliano Conde

 This file is part of jbilling.

 jbilling is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 jbilling is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with jbilling.  If not, see <http://www.gnu.org/licenses/>.

 This source was modified by Web Data Technologies LLP (www.webdatatechnologies.in) since 15 Nov 2015.
 You may download the latest source from webdataconsulting.github.io.

 */

package com.sapienter.jbilling.server.invoice;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO;
import com.sapienter.jbilling.server.item.ItemBL;
import com.sapienter.jbilling.server.util.ServerConstants;

import java.util.Comparator;

/**
 * @author Emil
 */
public class InvoiceLineComparator implements Comparator<InvoiceLineDTO> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(InvoiceLineDTO perA, InvoiceLineDTO perB) {
        int retValue;
        
        // the line type should tell first
        if (perA.getOrderPosition() == perB.getOrderPosition()) {
            
            try {
                if (perA.getTypeId() ==
                        ServerConstants.INVOICE_LINE_TYPE_SUB_ACCOUNT &&
                        perB.getTypeId() ==
                            ServerConstants.INVOICE_LINE_TYPE_SUB_ACCOUNT) {
                    // invoice lines have to be grouped by user
                    // find out both users
                    retValue = perA.getSourceUserId().compareTo(perB.getSourceUserId());
                    /*
                    new FormatLogger(InvoiceLineComparator.class).debug(
                            "Testing two sub account lines. a.userid " + 
                            perA.getSourceUserId() + " b.userid " + perB.getSourceUserId() +
                            " result " + retValue);
                            */
                    if (retValue != 0) {
                        // these are lines for two different users, so 
                        // they are different enough now
                        return retValue;
                    }
                } 
                // use the number
                if (perA.getItem() != null && perB.getItem() != null) {
                    ItemBL itemA = new ItemBL(perA.getItem());
                    ItemBL itemB = new ItemBL(perB.getItem());
                    if (itemA.getEntity().getNumber() == null &&
                            itemB.getEntity().getNumber() == null) {
                        retValue = new Integer(perA.getItem().getId()).compareTo(
                                new Integer(perB.getItem().getId()));
                    } else if (itemA.getEntity().getNumber() == null) {
                        retValue = 1;
                    } else if (itemB.getEntity().getNumber() == null) {
                        retValue = -1;
                    } else {
                        // none are null
                        retValue = itemA.getEntity().getNumber().compareTo(
                                itemB.getEntity().getNumber());
                    }
                } else {
                    retValue = 0;
                }
            } catch (Exception e) {
                new FormatLogger(InvoiceLineComparator.class).error(
                        "Comparing invoice lines " + perA + " " + perB, e);
                retValue = 0;
            }
        } else {
            retValue = new Integer(perA.getOrderPosition()).compareTo(perB.getOrderPosition());
        }
/*        
        new FormatLogger(InvoiceLineComparator.class).debug(
                "Comparing " + perA.getId() + " " + perB.getId() +
                " result " + retValue);
*/        
        return retValue;
    }

}
