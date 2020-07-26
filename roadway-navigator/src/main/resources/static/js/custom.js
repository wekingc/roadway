window.custom = true;

validateURL = function (url) {
    var exp = /^(https{0,1}:\/\/)([\S]+)$/i;
    return exp.test(url);
}

preventDefault = function (e) {
    try {
        if (e.preventDefault) e.preventDefault();
        else e.returnValue = false;
    } catch (e) { }
}

target = function (e) {
    try {
        return e.srcElement || e.target;
    } catch (e) {
        return this;
    }
}

getExpDate = function (days, hours, minutes) {
    var expDate = new Date();
    if (typeof (days) == "number" && typeof (hours) == "number" && typeof (hours) == "number") {
        expDate.setDate(expDate.getDate() + parseInt(days));
        expDate.setHours(expDate.getHours() + parseInt(hours));
        expDate.setMinutes(expDate.getMinutes() + parseInt(minutes));
        return expDate.toGMTString();
    }
}

//utility function called by getCookie()
getCookieVal = function (offset) {
    var endstr = document.cookie.indexOf(";", offset);
    if (endstr == -1) {
        endstr = document.cookie.length;
    }
    return unescape(document.cookie.substring(offset, endstr));
}

getCookie = function (name) {
    var arg = name + "=";
    var alen = arg.length;
    var clen = document.cookie.length;
    var i = 0;
    while (i < clen) {
        var j = i + alen;
        if (document.cookie.substring(i, j) == arg) {
            return getCookieVal(j);
        }
        i = document.cookie.indexOf(" ", i) + 1;
        if (i == 0) break;
    }
    return;
}

// store cookie value with optional details as needed
setCookie = function (name, value, expires, path, domain, secure) {
    document.cookie = name + "=" + escape(value) +
        ((expires) ? "; expires=" + expires : "") +
        ((path) ? "; path=" + path : "") +
        ((domain) ? "; domain=" + domain : "") +
        ((secure) ? "; secure" : "");
}

// remove the cookie by setting ancient expiration date
deleteCookie = function (name, path, domain) {
    if (getCookie(name)) {
        document.cookie = name + "=" +
            ((path) ? "; path=" + path : "") +
            ((domain) ? "; domain=" + domain : "") +
            "; expires=Thu, 01-Jan-70 00:00:01 GMT";
    }
}


if (window.$) {
    if (window.CryptoJS) {
        $.md5 = function (word) {
            return CryptoJS.MD5(word).toString();
        }
    }

    $.postForm = function (options) {
        var headers = {};
        var token = getCookie("token");
        if (!!token)
            headers.Authorization = "Bearer " + token;
        var data = {};
        var modify = options.modify || function (data) {
            return data;
        }
        var value;
        $("input[type=text],input[type=password],input[type=email],input[type=hidden],select,textarea").each(function (i, e) {
            if (e.dataset && e.dataset.type == "number") {
                value = e.value * 1;
            } else {
                value = e.value;
            }
            data[e.name || e.id] = value;
        });

        var cbParams = {};
        $("input[type=checkbox],input[type=radio]").filter(":checked").each(function (i, e) {
            var name = e.name || e.id;
            if (cbParams[name] != null) {
                cbParams[name].push(e.value);
            } else {
                cbParams[name] = [e.value];
            }
        });

        Object.keys(cbParams).forEach(function (key) {
            data[key] = cbParams[key].join(",");
        })

        options.contentType = options.contentType || "application/x-www-form-urlencoded; charset=UTF-8";
        options.type = options.type || "post";
        options.dataType = options.dataType || "json";
        options.cache = !!options.cache;
        options.async = !!options.async;
        options.url = options.url;

        data = $.extend({},options.data,data);
        data = modify(data);
        Object.keys(data).forEach(function (key) {
               if($.trim(key) == "") delete data[key];
         })

        if (options.contentType == "application/json") {
            options.data = JSON.stringify(data);
        }
        else {
            options.data = data;
        }

        options.headers = $.extend({}, options.headers, headers);
        $.ajax(options);
    }

    authorize = function () {
        $.postForm({
            url: "/api/roadway/auth",
            success: function (data) {
                if(data.code !== 200) {
                      window.location.replace("login.html");
                }
            }
        })
    }

    //回车焦点定位
    $(document).keypress(function (e) {
        e = event || e;
        var that = event.srcElement || event.target;
        var next = false;
        if (e.keyCode == 13 &&
            that.type != 'button' && that.type != 'submmit' &&
            (that.tagName == 'INPUT' || that.tagName == 'TEXTAREA' || that.tagName == 'BUTTON')
        ) {
            $("input,textarea,button").each(function (i, el) {
                if (next) {
                    el.focus();
                    preventDefault(event);
                    return false;
                }
                next = (el == that);
            });
        }
    })
}

function getAccount() {
    var token = getCookie("token");
    if(!token) return null;
    var base64 = new Base64();
    token = token.split(".")[1].replace("%", "").replace(",", "").replace(" ", "+");
    var lengthMod4 = token.length % 4;
     if (lengthMod4 > 0) {
        for(var i =0; i <  4 -lengthMod4;i++) {
            token += "=";
        }
     }
    var acc = JSON.parse(base64.decode(token));
    return acc;
}

function locate(url) {
    var title = $(target(event)).text();
    $.postForm({
        url: url,
        type: "get",
        dataType: "text",
        beforeSend: function () {
            $(".content").html('');
            $("h4.page-title").html(title);
        },
        success: function (data) {
            $(".content").html(data);
        }
    })
}