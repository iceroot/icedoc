package com.icedoc.gen;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.icedoc.doc.Doc;
import com.icedoc.doc.Docu;
import com.icedoc.doc.WordUtils;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;

public class DocService {

    public static void service(String folder, String folderOut, String host, String postType) {
        DocContext.setHost(host);
        DocContext.setPostType(postType);
        File folderFile = new File(folder);
        Map<String, String> paramNames = ParamConf.getParamNames();
        scan(folderFile, folderOut, paramNames);
        String basePathConf = DocFileUtil.getBasePathConf();
        String confText = basePathConf + "/src/conf.txt";
        String title = "";
        String version = "1.0";
        String author = "xxxx";
        if (FileUtil.exist(confText)) {
            Setting setting = new Setting("conf.txt");
            title = setting.getStr("title");
            version = setting.getStr("version");
            author = setting.getStr("author");
        }
        DocContext.setTitle(title);
        DocContext.setVersion(version);
        folderOut = StrUtil.removeSuffix(folderOut, "/");
        List<Doc> docList = DocContext.getDocList();
        String basePath = DocContext.getBasePath();
        String projectName = DocFileUtil.getProjectName(basePath);
        Docu docu = new Docu();
        if (StrUtil.isBlank(title)) {
            title = projectName;
        }
        String fileName = folderOut + "/" + title + ".doc";
        docu.setDate(DocFileUtil.now());
        docu.setTitle(title);
        docu.setVersion(version);
        docu.setAuthor(author);
        docu.setDocList(docList);
        Map<String, Object> map = ParamUtils.map(docu);
        FileUtil.mkParentDirs(fileName);
        String normalize = FileUtil.normalize(new File(".").getAbsolutePath());
        String simpleName = "doc";
        String xmlBasePath = normalize += "/src/com/icedoc/doc";
        String xmlPath = xmlBasePath + "/" + simpleName + ".xml";
        String ftlPath = xmlBasePath + "/" + simpleName + ".ftl";
        String xmlDestPathLeft = StringUtils.substringBeforeLast(xmlBasePath, "src");
        String xmlDestPathRight = StringUtils.substringAfterLast(xmlBasePath, "src");
        String xmlDestPath = StringUtils.removeEnd(xmlDestPathLeft, "/") + "/" + "bin" + "/"
                + StringUtils.removeStart(xmlDestPathRight, "/");
        String destPath = xmlDestPath + "/" + simpleName + ".ftl";
        FileUtil.copy(xmlPath, ftlPath, true);
        FileUtil.copy(xmlPath, destPath, true);
        String simpleFileName = simpleName + ".ftl";
        WordUtils.doc(fileName, map, simpleFileName);
    }

    private static String getWebName(String host, String projectName) {
        if (host == null) {
            host = "";
        }
        String oldHost = host;
        if (host.startsWith("http://")) {
            host = StringUtils.removeStart(host, "http://");
        }
        if (host.startsWith("https://")) {
            host = StringUtils.removeStart(host, "https://");
        }
        String webName = StringUtils.substringAfter(host, "/");
        host = StringUtils.removeEnd(oldHost, "/").trim();
        if (StringUtils.isBlank(webName)) {
            host = host + "/" + projectName;
        }
        return host;
    }

    private static void scan(File folderFile, String folderOut, Map<String, String> paramNames) {
        if (folderFile.isFile()) {
            String simpleName = folderFile.getName();
            if (simpleName.endsWith(".java")) {
                String fullName = DocFileUtil.getName(folderFile);
                List<String> readLines = FileUtil.readLines(folderFile, "UTF-8");
                if (DocFileUtil.contain(readLines, "@Controller")) {
                    String className = DocFileUtil.getPackageName(readLines);
                    String basePath = DocFileUtil.getBasePath(fullName, className);
                    DocContext.setBasePath(basePath);
                    String projectName = DocFileUtil.getProjectName(basePath);
                    String host = DocContext.getHost();
                    String hostWebName = getWebName(host, projectName);
                    DocContext.setHost(hostWebName);
                    String postType = DocContext.getPostType();
                    String[] exceptClass = { "Resource", "Controller", "RequestMapping", "ModelAndView" };
                    String[] exceptReturnClass = { "String", "JSONArray", "JSONObject", "ModelAndView", "Object" };
                    String[] exceptParamClass = { "HttpServletRequest", "HttpServletResponse", "Model" };
                    SimpleService.gen(fullName, hostWebName, exceptClass, exceptReturnClass, exceptParamClass, postType,
                            paramNames);
                }
            }
        } else if (folderFile.isDirectory()) {
            File[] listFiles = folderFile.listFiles();
            for (File file : listFiles) {
                scan(file, folderOut, paramNames);
            }
        } else {
            if (!FileUtil.exist(folderFile)) {
                throw new RuntimeException("文件不存在：" + folderFile);
            }
            throw new RuntimeException("不是一个有效的文件：" + folderFile);
        }

    }

}
