package com.cowave.commons.client.http.constants;

/**
 *
 * @author shanhuiming
 *
 */
public interface HttpHeader {

    /* ********************************************************************************
     * Authentication
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/www-authenticate">WWW-Authenticate</a>
     */
    String WWW_Authenticate = "WWW-Authenticate";

    /**
     * @see <a href="https://http.dev/authorization">Authorization</a>
     */
    String Authorization = "Authorization";

    /**
     * @see <a href="https://http.dev/proxy-authenticate">Proxy-Authenticate</a>
     */
    String Proxy_Authenticate = "Proxy-Authenticate";

    /**
     * @see <a href="https://http.dev/proxy-authorization">Proxy-Authorization</a>
     */
    String Proxy_Authorization = "Proxy-Authorization";

    /* ********************************************************************************
     * Caching
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/age">Age</a>
     */
    String Age = "Age";

    /**
     * @see <a href="https://http.dev/cache-control">Cache-Control</a>
     */
    String Cache_Control = "Cache-Control";

    /**
     * @see <a href="https://http.dev/clear-site-data">Clear-Site-Data</a>
     */
    String Clear_Site_Data = "Clear-Site-Data";

    /**
     * @see <a href="https://http.dev/expires">Expires</a>
     */
    String Expires = "Expires";

    /**
     * @see <a href="https://http.dev/pragma">Pragma</a>
     */
    String Pragma = "Pragma";

    /**
     * @see <a href="https://http.dev/warning">Warning</a>
     */
    String Warning = "Warning";

    /* ********************************************************************************
     * Client Hints
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/accept-ch">Accept-CH</a>
     */
    String Accept_CH = "Accept-CH";

    /* ********************************************************************************
     * Network client hints
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/save-data">Save-Data</a>
     */
    String Save_Data = "Save-Data";

    /* ********************************************************************************
     * Conditionals
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/last-modified">Last-Modified</a>
     */
    String Last_Modified = "Last-Modified";

    /**
     * @see <a href="https://http.dev/etag">ETag</a>
     */
    String ETag = "ETag";

    /**
     * @see <a href="https://http.dev/if-match">If-Match</a>
     */
    String If_Match = "If-Match";

    /**
     * @see <a href="https://http.dev/if-none-match">If-None-Match</a>
     */
    String If_None_Match = "If-None-Match";

    /**
     * @see <a href="https://http.dev/if-modified-since">If-Modified-Since</a>
     */
    String If_Modified_Since = "If-Modified-Since";

    /**
     * @see <a href="https://http.dev/if-unmodified-since">If-Unmodified-Since</a>
     */
    String If_Unmodified_Since = "If-Unmodified-Since";

    /**
     * @see <a href="https://http.dev/vary">Vary</a>
     */
    String Vary = "Vary";

    /**
     * @see <a href="https://http.dev/delta-base">Delta-Base</a>
     */
    String Delta_Base = "Delta-Base";

    /* ********************************************************************************
     * Connection Management
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/connection">Connection</a>
     */
    String Connection = "Connection";

    /**
     * @see <a href="https://http.dev/keep-alive">Keep-Alive</a>
     */
    String Keep_Alive = "Keep-Alive";

    /* ********************************************************************************
     * Content negotiation
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/accept">Accept</a>
     */
    String Accept = "Accept";

    /**
     * @see <a href="https://http.dev/accept-encoding">Accept-Encoding</a>
     */
    String Accept_Encoding = "Accept-Encoding";

    /**
     * @see <a href="https://http.dev/accept-language">Accept-Language</a>
     */
    String Accept_Language = "Accept-Language";

    /**
     * @see <a href="https://http.dev/a-im">A-IM</a>
     */
    String A_IM = "A-IM";

    /**
     * @see <a href="https://http.dev/im">IM</a>
     */
    String IM = "IM";

    /* ********************************************************************************
     * Controls
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/expect">Expect</a>
     */
    String Expect = "Expect";

    /**
     * @see <a href="https://http.dev/max-forwards">Max-Forwards</a>
     */
    String Max_Forwards = "Max-Forwards";

    /* ********************************************************************************
     * Cookies
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/cookie">Cookie</a>
     */
    String Cookie = "Cookie";

    /**
     * @see <a href="https://http.dev/set-cookie">Set-Cookie</a>
     */
    String Set_Cookie = "Set-Cookie";

    /* ********************************************************************************
     * CORS
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/access-control-allow-origin">Access-Control-Allow-Origin</a>
     */
    String Access_Control_Allow_Origin = "Access-Control-Allow-Origin";

    /**
     * @see <a href="https://http.dev/access-control-allow-credentials">Access-Control-Allow-Credentials</a>
     */
    String Access_Control_Allow_Credentials = "Access-Control-Allow-Credentials";

    /**
     * @see <a href="https://http.dev/access-control-allow-headers">Access-Control-Allow-Headers</a>
     */
    String Access_Control_Allow_Headers = "Access-Control-Allow-Headers";

    /**
     * @see <a href="https://http.dev/access-control-expose-headers">Access-Control-Expose-Headers</a>
     */
    String Access_Control_Expose_Headers = "Access-Control-Expose-Headers";

    /**
     * @see <a href="https://http.dev/access-control-max-age">Access-Control-Max-Age</a>
     */
    String Access_Control_Max_Age = "Access-Control-Max-Age";

    /**
     * @see <a href="https://http.dev/access-control-request-headers">Access-Control-Request-Headers</a>
     */
    String Access_Control_Request_Headers = "Access-Control-Request-Headers";

    /**
     * @see <a href="https://http.dev/access-control-request-method">Access-Control-Request-Method</a>
     */
    String Access_Control_Request_Method = "Access-Control-Request-Method";

    /**
     * @see <a href="https://http.dev/timing-allow-origin">Timing-Allow-Origin</a>
     */
    String Timing_Allow_Origin = "Timing-Allow-Origin";

    /* ********************************************************************************
     * Downloads
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/content-disposition">Content-Disposition</a>
     */
    String Content_Disposition = "Content-Disposition";

    /* ********************************************************************************
     * Message body information
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/content-length">Content-Length</a>
     */
    String Content_Length = "Content-Length";

    /**
     * @see <a href="https://http.dev/content-type">Content-Type</a>
     */
    String Content_Type = "Content-Type";

    /**
     * @see <a href="https://http.dev/content-encoding">Content-Encoding</a>
     */
    String Content_Encoding = "Content-Encoding";

    /**
     * @see <a href="https://http.dev/content-language">Content-Language</a>
     */
    String Content_Language = "Content-Language";

    /**
     * @see <a href="https://http.dev/content-location">Content-Location</a>
     */
    String Content_Location = "Content-Location";

    /* ********************************************************************************
     * Proxies
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/forwarded">Forwarded</a>
     */
    String Forwarded = "Forwarded";

    /**
     * @see <a href="https://http.dev/x-forwarded-for">X-Forwarded-For</a>
     */
    String X_Forwarded_For = "X-Forwarded-For";

    /**
     * @see <a href="https://http.dev/x-forwarded-host">X-Forwarded-Host</a>
     */
    String X_Forwarded_Host = "X-Forwarded-Host";

    /**
     * @see <a href="https://http.dev/x-forwarded-proto">X-Forwarded-Proto</a>
     */
    String X_Forwarded_Proto = "X-Forwarded-Proto";

    /**
     * @see <a href="https://http.dev/via">Via</a>
     */
    String Via = "Via";

    /* ********************************************************************************
     * Redirects
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/location">Location</a>
     */
    String Location = "Location";

    /* ********************************************************************************
     * Request context
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/from">From</a>
     */
    String From = "From";

    /**
     * @see <a href="https://http.dev/host">Host</a>
     */
    String Host = "Host";

    /**
     * @see <a href="https://http.dev/referer">Referer</a>
     */
    String Referer = "Referer";

    /**
     * @see <a href="https://http.dev/referrer-policy">Referrer-Policy</a>
     */
    String Referrer_Policy = "Referrer-Policy";

    /**
     * @see <a href="https://http.dev/user-agent">User-Agent</a>
     */
    String User_Agent = "User-Agent";

    /* ********************************************************************************
     * Response context
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/allow">Allow</a>
     */
    String Allow = "Allow";

    /**
     * @see <a href="https://http.dev/server">Server</a>
     */
    String Server = "Server";

    /* ********************************************************************************
     * Range requests
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/accept-ranges">Accept-Ranges</a>
     */
    String Accept_Ranges = "Accept-Ranges";

    /**
     * @see <a href="https://http.dev/range">Range</a>
     */
    String Range = "Range";

    /**
     * @see <a href="https://http.dev/if-range">If-Range</a>
     */
    String If_Range = "If-Range";

    /**
     * @see <a href="https://http.dev/content-range">Content-Range</a>
     */
    String Content_Range = "Content-Range";

    /* ********************************************************************************
     * Security
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/cross-origin-embedder-policy">Cross-Origin-Embedder-Policy</a>
     */
    String Cross_Origin_Embedder_Policy = "Cross-Origin-Embedder-Policy";

    /**
     * @see <a href="https://http.dev/cross-origin-opener-policy">Cross-Origin-Opener-Policy</a>
     */
    String Cross_Origin_Opener_Policy = "Cross-Origin-Opener-Policy";

    /**
     * @see <a href="https://http.dev/cross-origin-resource-policy">Cross-Origin-Resource-Policy</a>
     */
    String Cross_Origin_Resource_Policy = "Cross-Origin-Resource-Policy";

    /**
     * @see <a href="https://http.dev/content-security-policy">Content-Security-Policy</a>
     */
    String Content_Security_Policy = "Content-Security-Policy";

    /**
     * @see <a href="https://http.dev/content-security-policy-report-only">Content-Security-Policy-Report-Only</a>
     */
    String Content_Security_Policy_Report_Only = "Content-Security-Policy-Report-Only";

    /**
     * @see <a href="https://http.dev/expect-ct">Expect-CT</a>
     */
    String Expect_CT = "Expect-CT";

    /**
     * @see <a href="https://http.dev/strict-transport-security">Strict-Transport-Security</a>
     */
    String Strict_Transport_Security = "Strict-Transport-Security";

    /**
     * @see <a href="https://http.dev/upgrade-insecure-requests">Upgrade-Insecure-Requests</a>
     */
    String Upgrade_Insecure_Requests = "Upgrade-Insecure-Requests";

    /**
     * @see <a href="https://http.dev/x-content-type-options">X-Content-Type-Options</a>
     */
    String X_Content_Type_Options = "X-Content-Type-Options";

    /**
     * @see <a href="https://http.dev/x-frame-options">X-Frame-Options</a>
     */
    String X_Frame_Options = "X-Frame-Options";

    /**
     * @see <a href="https://http.dev/x-powered-by">X-Powered-By</a>
     */
    String X_Powered_By = "X-Powered-By";

    /**
     * @see <a href="https://http.dev/x-xss-protection">X-XSS-Protection</a>
     */
    String X_XSS_Protection = "X-XSS-Protection";

    /* ********************************************************************************
     * Fetch metadata request headers
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/sec-fetch-site">Sec-Fetch-Site</a>
     */
    String Sec_Fetch_Site = "Sec-Fetch-Site";

    /**
     * @see <a href="https://http.dev/sec-fetch-mode">Sec-Fetch-Mode</a>
     */
    String Sec_Fetch_Mode = "Sec-Fetch-Mode";

    /**
     * @see <a href="https://http.dev/sec-fetch-user">Sec-Fetch-User</a>
     */
    String Sec_Fetch_User = "Sec-Fetch-User";

    /**
     * @see <a href="https://http.dev/sec-fetch-dest">Sec-Fetch-Dest</a>
     */
    String Sec_Fetch_Dest = "Sec-Fetch-Dest";

    /* ********************************************************************************
     * Server-Sent events
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/nel">NEL</a>
     */
    String NEL = "NEL";

    /* ********************************************************************************
     * Transfer coding
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/transfer-encoding">Transfer-Encoding</a>
     */
    String Transfer_Encoding = "Transfer-Encoding";

    /**
     * @see <a href="https://http.dev/te">TE</a>
     */
    String TE = "TE";

    /**
     * @see <a href="https://http.dev/trailer">Trailer</a>
     */
    String Trailer = "Trailer";

    /* ********************************************************************************
     * WebSockets
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/sec-websocket-accept">Sec-Websocket-Accept</a>
     */
    String Sec_Websocket_Accept = "Sec-Websocket-Accept";

    /* ********************************************************************************
     * Other
     * ********************************************************************************/

    /**
     * @see <a href="https://http.dev/alt-svc">Alt-Svc</a>
     */
    String Alt_Svc = "Alt-Svc";

    /**
     * @see <a href="https://http.dev/date">Date</a>
     */
    String Date = "Date";

    /**
     * @see <a href="https://http.dev/link">Link</a>
     */
    String Link = "Link";

    /**
     * @see <a href="https://http.dev/retry-after">Retry-After</a>
     */
    String Retry_After = "Retry-After";

    /**
     * @see <a href="https://http.dev/server-timing">Server-Timing</a>
     */
    String Server_Timing = "Server-Timing";

    /**
     * @see <a href="https://http.dev/sourcemap">Sourcemap</a>
     */
    String Sourcemap = "Sourcemap";

    /**
     * @see <a href="https://http.dev/upgrade">Upgrade</a>
     */
    String Upgrade = "Upgrade";

    /**
     * @see <a href="https://http.dev/x-dns-prefetch-control">X-DNS-Prefetch-Control</a>
     */
    String X_DNS_Prefetch_Control = "X-DNS-Prefetch-Control";

    /**
     * @see <a href="https://http.dev/x-request-id">X-Request-ID</a>
     */
    String X_Request_ID = "X-Request-ID";

    /**
     * @see <a href="https://http.dev/x-robots-tag">X-Robots-Tag</a>
     */
    String X_Robots_Tag = "X-Robots-Tag";

    /**
     * @see <a href="https://http.dev/x-ua-compatible">X-UA-Compatible</a>
     */
    String X_UA_Compatible = "X-UA-Compatible";
}
