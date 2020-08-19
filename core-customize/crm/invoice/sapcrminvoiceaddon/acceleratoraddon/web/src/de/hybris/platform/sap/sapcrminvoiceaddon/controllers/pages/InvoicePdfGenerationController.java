/*
 *
 *  [y] hybris Platform
 *
 *  Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 *  This software is the confidential and proprietary information of SAP
 *  ("Confidential Information"). You shall not disclose such Confidential
 *  Information and shall use it only in accordance with the terms of the
 *  license agreement you entered into with SAP.
 * /
 */

package de.hybris.platform.sap.sapcrminvoiceaddon.controllers.pages;

import de.hybris.platform.sap.sapcrminvoiceaddon.controllers.SapcrminvoiceaddonControllerConstants;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import de.hybris.platform.util.Config;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.sap.sapinvoicefacades.facade.InvoiceFacade;
import org.apache.log4j.Logger;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.sap.sapinvoicefacades.exception.UnableToRetrieveInvoiceException;

import java.io.IOException;

/**
 * @author C5230258
 *
 */

@Controller("InvoicePdfGenrationController")
@Scope("tenant")
@RequestMapping("/my-account/invoicedocument")
public class InvoicePdfGenerationController {

    @Resource(name = "invoiceFacade")
    private InvoiceFacade invoiceFacade;
    @Autowired
    private SessionService sessionService;

    byte[] pdfData = null;
    private static final Logger logger = Logger.getLogger(InvoicePdfGenerationController.class.getName());

    @SuppressWarnings("boxing")
    @ResponseBody
    @RequestMapping(value = "/invoicedownload", method = { RequestMethod.GET, RequestMethod.POST })
    @RequireHardLogIn
    public void invoiceDownload(@RequestParam("invoiceCode")
    final String invoiceCode, final HttpServletResponse response) {
        boolean checkInvoiceCode = invoiceCode
                .matches(Config.getParameter(SapcrminvoiceaddonControllerConstants.CHECK_INVOICECODE_PATTERN));

        String invoiceCodeData = sessionService.getAttribute(invoiceCode + "_data");
        if (invoiceCodeData != null && checkInvoiceCode) {
            try {

                writePdfResponse(invoiceCode, response);
            }

            catch (UnableToRetrieveInvoiceException | IOException e) {
                logger.error("Error in invoiceDownload method::", e);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            }

        } else {
            logger.debug("Error in invoiceDownload method::Wrong document Number for Order ");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

        }

    }

    /**
     * @param invoiceCode
     * @param response
     * @throws IOException
     */
    private void writePdfResponse(final String invoiceCode, final HttpServletResponse response)
            throws IOException, UnableToRetrieveInvoiceException {
        if (sessionService.getAttribute(invoiceCode) != null) {
            pdfData = (byte[]) (sessionService.getAttribute(invoiceCode));
        } else {
            pdfData = (byte[]) (invoiceFacade.generatePdf(invoiceCode));

        }

        final String invoiceName = invoiceCode.concat("_invoice.pdf");
        if (null != pdfData && pdfData.length > 0) {
            response.setHeader("Content-Disposition", "inline;filename=" + invoiceName);
            response.setDateHeader("Expires", -1);
            response.setContentType("application/pdf");
            response.getOutputStream().write(pdfData);
            response.getOutputStream().close();
            sessionService.setAttribute(invoiceCode, pdfData);
        } else {
            logger.debug("Error in writePdfResponse method::Document not available for Order ");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
