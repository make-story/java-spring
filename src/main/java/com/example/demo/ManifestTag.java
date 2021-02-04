package com.example.demo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/*
<dependency>
	<groupId>com.googlecode.json-simple</groupId>
	<artifactId>json-simple</artifactId>
	<version>1.1</version>
</dependency>
*/
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

// JSP 2.0 이상의 SimpleTagSupport를 이용하여 커스텀태그를 개발
// JSP 버전 확인 : <%= JspFactory.getDefaultFactory().getEngineInfo().getSpecificationVersion() %>
/*
1. SimpleTagSupport태그 핸들러를 상속받아 원하는 기능을 구현합니다.
2. TLD는 Tag Library Descriptor로 작성한 태그에 대한 uri와 태그명 등을 정하는 설정파일이며 *.tld파일을 작성하여 서블릿컨테이너가 인식하는 webapp/WEB-INF/ 하위 경로에 넣습니다. (JSP 1.2 버전 이하에서는 web.xml에 추가적인 설정을 통해 인식을 시켜주어야 하지만 JSP 2.0부터는 경로에만 넣어주면 자동 인식됩니다.)
3. JSP에서 커스텀 태그를 사용하기 위해 taglib 디렉티브(<%@ taglib %>) 를 상단에 선언하고 사용합니다.
*/
public class ManifestTag extends SimpleTagSupport {
	
	//@Autowired 
	//private ServletContext servletContext; // 웹 루트 절대경로 정보 
	//@Autowired 
	//private ResourceLoader resourceLoader; // 자원(다양한 타입의 파일들)을 기준으로 경로 찾기

	// 커스텀 태그속성 관련 
    private String tool; // gulp, webpack
    private String filename; // 매니페스트 파일명
    private String type; // css, js, json 
    
    // 경로확인 후 폴더 생성  
    public void mkdir(String path) {
    	path = "D:\\Eclipse\\Java\\새폴더"; //폴더 경로
    	File Folder = new File(path);

    	// 해당 디렉토리가 없을경우 디렉토리를 생성합니다.
    	if(!Folder.exists()) {
    		try {
    		    Folder.mkdir(); //폴더 생성합니다.
    		    System.out.println("폴더가 생성되었습니다.");
    		}catch(Exception e) {
    			e.getStackTrace();
    		}        
    	}else {
    		System.out.println("이미 폴더가 생성되어 있습니다.");
    	}
    }

    // doTag()를 오버라이딩 하여 커스텀 태그 처리 내용을 개발한다.
    @Override
    public void doTag() throws JspException, IOException {
        // JspContext는 이 태그를 사용한 JSP 페이지에 대한 정보를  담고 있다.  
        PageContext context = (PageContext)this.getJspContext();
        
        // 태그가 호출된 JSP에 대해 요청 정보를 가져온다.
        HttpServletRequest request =  (HttpServletRequest)context.getRequest();
        //System.out.println("태그가 호출된 JSP URI : " +  request.getRequestURI());
        
        // 태그를 사용할때 지정한 속성값들
        String tool = this.getTool();
        String filename = this.getFilename();
        String type = this.getType();
        
        // 커스텀 태그의 몸체를 담을 StringWriter
        //StringWriter stringWriter = new StringWriter();
        
        // invoke()가 실행되면 StringWriter의 몸체(<커트텀태그>몸체</커트텀태그>) 텍스트가 담긴다.
        //getJspBody().invoke(stringWriter);
        //String bodyText = stringWriter.toString();
        
        // Jsp 페이지의 결과를 브라우저로 출력할 출력스트림를 얻는다.
        // JSP의 out 객체와 같음.
        JspWriter out = context.getOut();
        //out.print("REACT");

        // 
        /*
        경로 수정이 필요하다! (톰캣 .war 빌드되면서 경로 변경됨) 
        /usr/local/server/apache-tomcat-8.5.11/conf/src/main/resources/static/manifest/react.json
        /usr/local/server/tomcat/webapps/ROOT/WEB-INF/resources
        */
        //final DefaultResourceLoader loader = new DefaultResourceLoader();                            
        //Resource resource = loader.getResource("file:src/main/resources/static/manifest/" + filename + ".json");
        //String manifestPath = resource.getFile().getAbsolutePath();
        //out.print("<div>" + manifestPath + "</div>");
        
        // tomcat war 실행 경로 
        /*
        절대경로
		this.getClass().getResource("").getPath(); // 현재 자신의 절대 경로
		this.getClass().getResource("/").getPath(); // classes 폴더의 최상위 경로
		this.getClass().getResource("/com/test/config/config.properties").getPath(); // classes 폴더에서부터 시작하여 해당파일까지의 절대 경로
         */
        URL classesPath = this.getClass().getResource("/"); // 예 : "file:/usr/local/server/apache-tomcat-8.5.11/webapps/ROOT/WEB-INF/classes/"
        //out.print("<!-- " + classesPath + " //-->");
        
        // 프론트 리소스 매니페스트(manifest) 파일 경로  
        final DefaultResourceLoader loader = new DefaultResourceLoader();
        String manifestPath = classesPath + "static/manifest/" + filename + ".json";
        Resource resource = loader.getResource(manifestPath);
        if(resource.exists()) {
        	/*
        	new File("").getAbsolutePath() : 절대경로
			new File("").getCanonicalPath() : 상대경로
        	*/
	        manifestPath = resource.getFile().getAbsolutePath();
	        out.print("<!-- manifest 파일존재 (" + manifestPath + ") //-->");
	        
	        // 매니페스트(manifest) 파일 가져오기 
	        //File file = new File(manifestPath);
	        //if(file.exists()) {
		        try {
		        	// 매니페스트 파일 내부 css, js 등 정보 읽어오기  
			        JSONParser jsonParser = new JSONParser();
					JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(manifestPath));
		        
					// 공통 경로 
					String publicPath = "";
					if(tool.equals("gulp")) {
			        	publicPath = "static/gulp/";
			        }else if(tool.equals("webpack")) {
			        	publicPath = "static/webpack/";
			        }
					
					// 태그 출력 
					if(type.equals("css") && jsonObject.containsKey("css")) {
						JSONArray jsonArray = (JSONArray) jsonObject.get("css");
						for(int i=0; i<jsonArray.size(); i++) {
			 				out.print("<link rel=\"stylesheet\" href=\"" + publicPath + jsonArray.get(i) + "\">");
			 			}
					}
			        if(type.equals("js") && jsonObject.containsKey("js")) {
			        	JSONArray jsonArray = (JSONArray) jsonObject.get("js");
				        for(int i=0; i<jsonArray.size(); i++) {
			 				out.print("<script src=\"" + publicPath + jsonArray.get(i) + "\"></script>");
			 			}
			        }
		        }catch (ParseException e) {
		 			// TODO Auto-generated catch block
		 			e.printStackTrace();
		 		}
	        //}
        }else {
        	out.print("<!-- manifest 파일없음 (" + manifestPath + ") -->");
        }
    }
    
    // getter / setter
    public String getTool() {
        return tool;
    }
    public void setTool(String tool) {
        this.tool = tool;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    private String getType() {
		return type;
	}
    public void setType(String type) {
        this.type = type;
    }
}
