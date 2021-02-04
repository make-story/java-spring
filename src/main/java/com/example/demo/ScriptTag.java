package com.example.demo;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

// JSP 2.0 이상의 SimpleTagSupport를 이용하여 커스텀태그를 개발
// JSP 버전 확인 : <%= JspFactory.getDefaultFactory().getEngineInfo().getSpecificationVersion() %>
/*
1. SimpleTagSupport태그 핸들러를 상속받아 원하는 기능을 구현합니다.
2. TLD는 Tag Library Descriptor로 작성한 태그에 대한 uri와 태그명 등을 정하는 설정파일이며 *.tld파일을 작성하여 서블릿컨테이너가 인식하는 webapp/WEB-INF/ 하위 경로에 넣습니다. (JSP 1.2 버전 이하에서는 web.xml에 추가적인 설정을 통해 인식을 시켜주어야 하지만 JSP 2.0부터는 경로에만 넣어주면 자동 인식됩니다.)
3. JSP에서 커스텀 태그를 사용하기 위해 taglib 디렉티브(<%@ taglib %>) 를 상단에 선언하고 사용합니다.
*/
@PropertySource("classpath:src/main/resources/custom.properties")
public class ScriptTag extends SimpleTagSupport{
	
	@Resource
    private Environment environment;

	private String tool;
	private String entry;

	// doTag()를 오버라이딩 하여 커스텀 태그 처리 내용을 개발한다.
	@Override
	public void doTag() throws JspException, IOException {
		// JspContext는 이 태그를 사용한 JSP 페이지에 대한 정보를  담고 있다.  
		PageContext context = (PageContext)this.getJspContext();
        
		// 태그가 호출된 JSP에 대해 요청 정보를 가져온다.
		HttpServletRequest request =  (HttpServletRequest)context.getRequest();
		System.out.println("태그가 호출된 JSP URI : " +  request.getRequestURI());
        
		// 태그를 사용할때 지정한 속성값들
		String tool = this.getTool();
		String entry = this.getEntry();
        
		// 커스텀 태그의 몸체를 담을 StringWriter
		StringWriter stringWriter = new StringWriter();
        
		// invoke()가 실행되면 StringWriter의 몸체(<커트텀태그>몸체</커트텀태그>) 텍스트가 담긴다.
		//getJspBody().invoke(stringWriter);
		//String bodyText = stringWriter.toString();
        
		// Jsp 페이지의 결과를 브라우저로 출력할 출력스트림를 얻는다.
		// JSP의 out 객체와 같음.
		JspWriter out = context.getOut();
        
		// 환경변수 접근!
		out.print(environment.getProperty("test"));
		
		// tool
		if(tool == "gulp") {
			try {
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("./target/classes/META-INF/resources/gulp/" + entry + ".json"));
				out.print("<script src=\"http://local-static.cjmall.net/gulp/" + jsonObject.get(entry + ".js") + "\"></script>");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(tool == "webpack") {
			try {
				JSONParser jsonParser = new JSONParser();
				JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("./target/classes/static/webpack/" + entry + ".json"));
				for(int i=0; i<jsonArray.size(); i++) {
					out.print("<script src=\"http://local-display.cjmall.com/webpack/" + jsonArray.get(i) + "\"></script>");
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    
	// getter / setter
	public String getTool() {
		return tool;
	}
	public void setTool(String tool) {
		this.tool = tool;
	}
	public String getEntry() {
		return entry;
	}
	public void setEntry(String entry) {
		this.entry = entry;
	}
}