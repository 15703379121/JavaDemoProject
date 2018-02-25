package com.example.administrator.javademo.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2018/2/12 0012.
 */

public class FileTypeUtil {
    public static final String[] VIDEO_EXTENSIONS = {  ".3gp", ".amv",
            ".avb", ".avd", ".avi", ".flh", ".fli", ".flv", ".flx", ".gvi", ".gvp",
            ".hdmov", ".hkm", ".ifo", ".imovi", ".iva", ".ivf", ".ivr",
            ".m4v", ".m75", ".meta", ".mgv", ".mj2", ".mjp", ".mjpg", ".mkv", ".mmv",
            ".mnv", ".mod", ".modd", ".moff", ".moi", ".moov", ".mov", ".movie",
             ".mp21", ".mp2v", ".mp4", ".mp4v", ".mpe", ".mpeg", ".mpeg4",
            ".mpf", ".mpg", ".mpg2", ".mpgin", ".mpl", ".mpls", ".mpv", ".mpv2", ".mqv",
            ".msdvd", ".msh", ".mswmm", ".mts", ".mtv", ".mvb", ".mvc", ".mvd", ".mve",
            ".mvp", ".mxf", ".mys", ".ncor", ".nsv", ".nvc", ".ogm", ".ogv", ".ogx",
            ".osp", ".par", ".pds", ".pgi", ".piv", ".playlist", ".pmf", ".prel",
            ".pro", ".prproj", ".psh", ".pva", ".pvr", ".pxv", ".qt", ".qtch", ".qtl",
            ".qtm", ".qtz", ".rcproject", ".rdb", ".rec", ".rm", ".rmd", ".rmp", ".rms",
            ".rmvb", ".roq", ".rp", ".rts",  ".rum", ".rv", ".sbk", ".sbt",
            ".scm", ".scn", ".sec", ".seq", ".sfvidcap", ".smil", ".smk",
            ".sml", ".smv", ".spl", ".ssm", ".str", ".stx", ".svi", ".swf", ".swi",
            ".swt", ".tda3mt", ".tivo", ".tix", ".tod", ".tp", ".tp0", ".tpd", ".tpr",
            ".trp", ".ts", ".tvs", ".vc1", ".vcr", ".vcv", ".vdo", ".vdr", ".veg",
            ".vem", ".vf", ".vfw", ".vfz", ".vgz", ".vid", ".viewlet", ".viv", ".vivo",
            ".wma"
    };
    //集合放置所有支持视频格式
    private static final List<String> listvideo = new ArrayList<>(
            Arrays.asList(VIDEO_EXTENSIONS));

    // 检测是否是视频文件
    public static boolean isVideo(String path) {
        path=getFileExtension(path);
        return listvideo.contains(path);
    }
    //   获取文件后缀名
    public static String getFileExtension(String path) {
        if (null != path) {
            // 后缀点 的位置
            int dex = path.lastIndexOf(".");
            // 截取后缀名
            return path.substring(dex);
        }
        return null;
    }
}
