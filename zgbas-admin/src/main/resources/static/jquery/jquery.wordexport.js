if (typeof jQuery !== "undefined" && typeof saveAs !== "undefined") {
    (function($) {
        $.fn.wordExport = function(fileName) {
            fileName = typeof fileName !== 'undefined' ? fileName : "合同";
            var static = {
                mhtml: {
                    top: "Mime-Version: 1.0\nContent-Base: " + location.href + "\nContent-Type: Multipart/related; boundary=\"NEXT.ITEM-BOUNDARY\";type=\"text/html\"\n\n--NEXT.ITEM-BOUNDARY\nContent-Type: text/html; charset=\"utf-8\"\nContent-Location: " + location.href + "\n\n<!DOCTYPE html>\n<html>\n_html_</html>",
                    head: "<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n<style>\n_styles_\n</style>\n</head>\n",
                    body: "<body>_body_</body>"
                }
            };
            var options = {
                maxWidth: 624
            };
            // Clone selected element before manipulating it
            var markup = $(this).clone();

            // Remove hidden elements from the output
            markup.each(function() {
                var self = $(this);
                if (self.is(':hidden'))
                    self.remove();
            });

            // Embed all images using Data URLs
            var images = Array();
            var img = markup.find('img');
            for (var i = 0; i < img.length; i++) {
                // Calculate dimensions of output image
                var w = Math.min(img[i].width, options.maxWidth);
                var h = img[i].height * (w / img[i].width);
                // Create canvas for converting image to data URL
                var canvas = document.createElement("CANVAS");
                canvas.width = w;
                canvas.height = h;
                // Draw image to canvas
                var context = canvas.getContext('2d');
                context.drawImage(img[i], 0, 0, w, h);
                // Get data URL encoding of image
                var uri = canvas.toDataURL("image/png");
                $(img[i]).attr("src", img[i].src);
                img[i].width = w;
                img[i].height = h;
                // Save encoded image to array
                images[i] = {
                    type: uri.substring(uri.indexOf(":") + 1, uri.indexOf(";")),
                    encoding: uri.substring(uri.indexOf(";") + 1, uri.indexOf(",")),
                    location: $(img[i]).attr("src"),
                    data: uri.substring(uri.indexOf(",") + 1)
                };
            }

            // Prepare bottom of mhtml file with image data
            var mhtmlBottom = "\n";
            for (var i = 0; i < images.length; i++) {
                mhtmlBottom += "--NEXT.ITEM-BOUNDARY\n";
                mhtmlBottom += "Content-Location: " + images[i].location + "\n";
                mhtmlBottom += "Content-Type: " + images[i].type + "\n";
                mhtmlBottom += "Content-Transfer-Encoding: " + images[i].encoding + "\n\n";
                mhtmlBottom += images[i].data + "\n\n";
            }
            mhtmlBottom += "--NEXT.ITEM-BOUNDARY--";

            //TODO: load css from included stylesheet
            var styles = "body{margin:0;padding:0;font-size: 12px;font-family:'Lantinghei SC', 'Open Sans', Arial, 'Hiragino Sans GB', 'Microsoft YaHei', 'Î¢ÈíÑÅºÚ', 'STHeiti', 'WenQuanYi Micro Hei', SimSun, sans-serif;}";
            styles+="img,input{vertical-align:middle;} ul{margin:0;padding: 0;} li{list-style: none;overflow: hidden;white-space: nowrap;text-overflow:ellipsis;}";
            styles+="i{font-style: normal;}  .contract p{text-indent: 2em;margin-top: 0;margin-bottom: 0;line-height: 26px;}";
            styles+=".contract p i{text-indent: initial;color: #2d2d2d}   .contract p i.goods-type{color: #999}   i.bank, i.account{min-width: 100px;}";
            styles+="i.address{width: 150px;}  .clearFix:after{content:''; display:block; clear:both;}  .contract{margin:20px auto;width: 800px;font-size: 14px;}";
            styles+=".contract > img{width: 100%;}  .contract h3{text-align:center;font-size:22px;font-weight:normal;padding-top:20px;padding-bottom:20px;}";
            styles+=".contract .con-tit{padding-left: 2em;}  .contract .con-tit li{float: right; clear: right; width: 200px; height: 30px; line-height: 30px;}";
            styles+=".contract .con-tit .con-tit-l{float: left; width: 350px;}  .contract .con-context{margin-top: 18px;}";
            styles+=".con-context .table-box {margin-left: 2em;margin-bottom: 15px;}";
            styles+=".con-context .table{width: 100%; display: table; border-top: 1px solid #2d2d2d; border-left: 1px solid #2d2d2d; text-align: center; font-size: 14px; color: #2d2d2d;position: relative;}";
            styles+=".con-context .table-row{display: table-row; height: 30px; line-height: 30px;}  .con-context .table-row:first-child {height: 34px;line-height: 34px;}";
            styles+=".con-context .table-row span{display: table-cell; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; border-right: 1px solid #2d2d2d; border-bottom: 1px solid #2d2d2d;}";
            styles+=".con-context .table-row .cell1{width: 11%;max-width: 70px;}   .con-context .table-row .cell2{width: 11%;max-width: 70px;}   .con-context .table-row .cell3{width: 14%;max-width: 94px;}";
            styles+=".con-context .table-row .cell4{width: 14%;max-width: 84px;}   .con-context .table-row .cell5{width: 16%;max-width: 90px;}   .con-context .table-row .cell6{width: 17%;max-width: 104px;}";
            styles+=".con-context .table-row .cell7{width: 17%;max-width: 122px;}  .con-context .table-row .t-btm1{width: 66%;position: absolute; text-align: left; border-right: none;padding-left: 10px;box-sizing: border-box;}";
            styles+=".con-context .table-row .t-btm2 {position: absolute; right: 0; text-align: left;width: 34%;}";
            styles+=".contract .con-chapter{border: 1px solid #2d2d2d; border-left: none; margin-left: 2em; margin-top: 15px;line-height: 30px;}";
            styles+=".contract .con-chapter ul{box-sizing: border-box;width: 50%; padding: 5px 10px; border-left: 1px solid #2d2d2d; float: left;}";
            styles+="html {-webkit-text-size-adjust: 100%; -webkit-tap-highlight-color: rgba(0, 0, 0, 0); }html, body, ul, li, ol, dl, dd, dt, p, h1, h2, h3, h4, h5, h6, form, legend { margin: 0; padding: 0; }   i{ font-style: normal;display: inline-block; }";
            styles+="table {border-collapse: collapse; border-spacing: 0; }   img { vertical-align: middle; border-style: none; }   input{ box-sizing: border-box; padding: 0;-webkit-appearance:none; }    ul, ol { list-style: none; }";
            styles+="abbr[title] { border-bottom: none; text-decoration: underline; }   article, aside, footer, header, nav, section { display: block; }    audio, video, canvas { display: inline-block; }    audio:not([controls]) { display: none; height: 0; }    button, input, select, textarea { font-family: sans-serif; font-size: 100%; line-height: 1.15; margin: 0;cursor: pointer; }";
            
            // Aggregate parts of the file together
            var fileContent = static.mhtml.top.replace("_html_", static.mhtml.head.replace("_styles_", styles) + static.mhtml.body.replace("_body_", markup.html())) + mhtmlBottom;

            // Create a Blob with the file contents
            var blob = new Blob([fileContent], {
                type: "application/msword;charset=utf-8"
            });
            saveAs(blob, fileName + ".doc");
        };
    })(jQuery);
} else {
    if (typeof jQuery === "undefined") {
        console.error("jQuery Word Export: missing dependency (jQuery)");
    }
    if (typeof saveAs === "undefined") {
        console.error("jQuery Word Export: missing dependency (FileSaver.js)");
    }
}
