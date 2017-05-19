package com.linsh.lshapp.model.bean.http;

/**
 * Created by Senh Linsh on 17/5/18.
 */

public class UploadInfo {

//    {
//        "code": 0,
//        "message": "SUCCESS",
//        "data": {
//            "access_url": "http://accesslog-10055004.file.myqcloud.com/testfolder/111.txt",
//                    "preview_url": "http://accesslog-10055004.preview.myqcloud.com/testfolder/111.txt?cmd=txt_preview",
//                    "resource_path": "/10055004/accesslog/testfolder/111.txt",
//                    "source_url": "http://accesslog-10055004.cossh.myqcloud.com/testfolder/111.txt",
//                    "url": "http://sh.file.myqcloud.com/files/v2/10055004/accesslog/testfolder/111.txt"
//        }
//    }

    public String access_url;
    public String preview_url;
    public String resource_path;
    public String source_url;
    public String url;
}
