package com.bike.store.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Value("${social.instagram}")
    private String socialInstagram;

    @Value("${social.youtube}")
    private String socialYouTube;

    @Value("${social.facebook}")
    private String socialFacebook;

    @Value("${social.twitter}")
    private String socialTwitter;

    // ===== GST CONFIGURATION =====
    @Value("${gst.gstin}")
    private String gstin;

    @Value("${gst.rate}")
    private String gstRate;

    @Value("${gst.hsn}")
    private String hsnCode;

    @Value("${gst.description}")
    private String gstDescription;

    // ===== COMPANY CONFIGURATION =====
    @Value("${company.name}")
    private String companyName;

    @Value("${company.email}")
    private String supportEmail;

    @Value("${company.year}")
    private String companyYear;

    @Value("${company.sub}")
    private String companySub;

    @Value("${app.default.currency}")
    private String defaultCurrency;

    @Value("${app.base.url}")
    private String appBaseUrl;

    @ModelAttribute("socialInstagram")
    public String getSocialInstagram() {
        return socialInstagram;
    }

    @ModelAttribute("socialYouTube")
    public String getSocialYouTube() {
        return socialYouTube;
    }

    @ModelAttribute("socialFacebook")
    public String getSocialFacebook() {
        return socialFacebook;
    }

    @ModelAttribute("socialTwitter")
    public String getSocialTwitter() {
        return socialTwitter;
    }

    @ModelAttribute("gstin")
    public String getGstin() {
        return gstin;
    }

    @ModelAttribute("gstRate")
    public Double getGstRate() {
        try {
            return Double.parseDouble(gstRate);
        } catch (NumberFormatException e) {
            return 18.0;
        }
    }

    @ModelAttribute("hsnCode")
    public String getHsnCode() {
        return hsnCode;
    }

    @ModelAttribute("gstDescription")
    public String getGstDescription() {
        return gstDescription;
    }

    @ModelAttribute("companyName")
    public String getCompanyName() {
        return companyName;
    }

    @ModelAttribute("supportEmail")
    public String getSupportEmail() {
        return supportEmail;
    }

    @ModelAttribute("companyYear")
    public String getCompanyYear() {
        return companyYear;
    }

    @ModelAttribute("companySub")
    public String getCompanySub() {
        return companySub;
    }

    @ModelAttribute("defaultCurrency")
    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    @ModelAttribute("appBaseUrl")
    public String getAppBaseUrl() {
        return appBaseUrl;
    }
}