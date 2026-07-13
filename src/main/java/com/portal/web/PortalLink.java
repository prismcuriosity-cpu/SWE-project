package com.portal.web;

/**
 * A single external quick-link shown on the Access Center page.
 *
 * @param title       the display name of the resource
 * @param url         the external URL the card points to
 * @param icon        an emoji used as a lightweight icon
 * @param description a short one-line explanation of the resource
 */
public record PortalLink(String title, String url, String icon, String description) {
}
