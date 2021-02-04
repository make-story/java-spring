package com.example.demo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.jar.Attributes;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
 
// JSP 2.0 이상의 SimpleTagSupport를 이용하여 커스텀태그를 개발
// JSP 버전 확인 : <%= JspFactory.getDefaultFactory().getEngineInfo().getSpecificationVersion() %> 
/*
1. SimpleTagSupport태그 핸들러를 상속받아 원하는 기능을 구현합니다.
2. TLD는 Tag Library Descriptor로 작성한 태그에 대한 uri와 태그명 등을 정하는 설정파일이며 *.tld파일을 작성하여 서블릿컨테이너가 인식하는 webapp/WEB-INF/ 하위 경로에 넣습니다. (JSP 1.2 버전 이하에서는 web.xml에 추가적인 설정을 통해 인식을 시켜주어야 하지만 JSP 2.0부터는 경로에만 넣어주면 자동 인식됩니다.)
3. JSP에서 커스텀 태그를 사용하기 위해 taglib 디렉티브(<%@ taglib %>) 를 상단에 선언하고 사용합니다.
*/

//스프링 resource폴더 안에 .properties를 정의
@Configuration
//-Dspring.profiles.active=p1
//@PropertySource("classpath:sample-${spring.profiles.active}.properties")
//하나의 properties를 사용할 경우
//@PropertySource("classpath:setting.properties")
//@PropertySource("file:/data/${APP_ENV:DEV}.setting.properties")
//여러 properties를 사용할 경우
//@PropertySource({"default.properties", "overriding.properties"})
/*@PropertySources({
	//${APP_ENV:DEV}의 의미는 환경변수의 APP_ENV 값을 찾아 대입 하라는 의미 이며, 환경변수의 값이 없을 경우 DEV 로 진행하라는 의미이다.
	@PropertySource("classpath:${APP_ENV:DEV}.db.properties"),
	@PropertySource("classpath:${APP_ENV:DEV}.setting.properties")
	//물리 위치에서 파일을 찾을 경우
	@PropertySource("file:/data/${APP_ENV:DEV}.setting.properties")
})*/
//@ConfigurationPropertiesScan("com.baeldung.properties")
public class CustomTag extends SimpleTagSupport implements EnvironmentAware {

    // 커스텀 태그 속성값 
	/*
	JSP 페이지 사용 예 
	<%@ taglib prefix="front" uri="http://www.cjoshopping.com/tags/resource/build" %>
	<front:script tool="gulp" filename="library"></front:script>
	<front:script tool="webpack" filename="homeTab"></front:script>
	*/
    private String color;
    private int iterNum;
    
	//@Value("${sbpg.init.welcome-message:Hello world}")
	//private String active;
	
	//@Value("${service.properties.is.working}")
    //private Boolean isWorking;
	
	//@Value("${spring.profile.value}")  // .properties 파일의 key에 해당하는 값을 주입
	//String test;
    
	//@Autowired
    //private Environment environment;
	
	@Autowired
	private static Environment environment;
    @Override
    public void setEnvironment(Environment environment) {
        // TODO Auto-generated method stub
        this.environment = environment;
    }
    
	// src/main/resources/ 내부 .properties 파일 읽기  
    public Properties getProperties(String resource) {
    	Properties prop = new Properties();
    	
    	//try(OutputStream output = new FileOutputStream("path/to/config.properties")) {
    	try(InputStream input = new FileInputStream("src/main/resources/" + resource)) {
            // load a properties file from InputStream
            prop.load(input);
            /*
            // get value by key
			prop.getProperty("db.url");
		    prop.getProperty("db.user");
		    prop.getProperty("db.password");
             */
            // Java 8 , print key and values
            prop.forEach((key, value) -> System.out.println("Key : " + key + ", Value : " + value));
        }catch(IOException e) {
        	//System.out.println("Properties File Load Fail!");
            //e.printStackTrace();
        }
    	
    	return prop;
    }
    
    // [중요!] 
    // 파일명.properties 자동으로 파일명 찾아 읽어오기
    // 파일 찾기 우선순위 1 : src/main/resources/파일명.properties
    // 파일 찾기 우선순위 2 : src/main/webapp/WEB-INF/classes/파일명.properties
    // 젠킨스 빌드시, 해당 위치(약속된 파일명)에 빌드와 관련된 정보(환경, 빌드번호 등)를 .properties 파일에 쓴다.  
    /*
    # "Java Web 작업 경로에 빌드관련(실행환경, 프론트정보 등) 프로퍼티를 주입합니다."
	# "echo -e 옵션: 문자열에서 역슬래시(\)와 조합되는 이스케이프 문자(escape sequence)를 인용부호(")로 묶어 인식"
	echo -e "hello="${ACTIVE}"\nACTIVE="${ACTIVE}"\nFRONT_TEST="${FRONT_TEST}"\nFRONT_STAGE="${FRONT_STAGE}"\nFRONT_PRODUCTION="${FRONT_PRODUCTION} > /usr/src/spring/demo.git/src/main/webapp/WEB-INF/classes/service.properties
    */
    public Properties getPropertiesStatic(String resource) {
    	// WEB-INF 아래 classes 폴더를 생성
    	// classes 폴더에 .properties 파일 생성 
    	// ClassLoader 를 통해 WEB-INF 하위 classes 폴더를 찾아 .properties 탐색 
    	// (즉, WEB-INF 하위 classes 폴더에 .properties 파일을 두면 ClassLoader 알아서 찾아온다.)
    	Properties prop = new Properties();
    	
        try {
        	prop.load(this.getClass().getClassLoader().getResourceAsStream(resource));
        }catch(IOException e) {
            //System.out.println("Properties File Load Fail!");
            //e.printStackTrace();
        }
        
        return prop;
    }
    
    // /META-INF/MANIFEST.MF 매니패스트 파일 읽기 
    public String getReadingVersion() throws IOException {
    	ExternalContext application = FacesContext.getCurrentInstance().getExternalContext();
    	InputStream inputStream = application.getResourceAsStream("/META-INF/MANIFEST.MF");
    	Manifest filename = new Manifest(inputStream);
    	Attributes attributes = filename.getMainAttributes();
    	String version = attributes.getValue("Implementation-Version");
    	
    	return version;
    }
    
    // json 파일 읽기/쓰기
    // https://howtodoinjava.com/library/json-simple-read-write-json-examples/
    /*
    <dependency>
		<groupId>com.googlecode.json-simple</groupId>
		<artifactId>json-simple</artifactId>
		<version>1.1</version>
	</dependency>
    */
    public void setJSONFile() {
    	/*
    	//First Employee
        JSONObject employeeDetails = new JSONObject();
        employeeDetails.put("firstName", "Lokesh");
        employeeDetails.put("lastName", "Gupta");
        employeeDetails.put("website", "howtodoinjava.com");
         
        JSONObject employeeObject = new JSONObject(); 
        employeeObject.put("employee", employeeDetails);
         
        //Second Employee
        JSONObject employeeDetails2 = new JSONObject();
        employeeDetails2.put("firstName", "Brian");
        employeeDetails2.put("lastName", "Schultz");
        employeeDetails2.put("website", "example.com");
         
        JSONObject employeeObject2 = new JSONObject(); 
        employeeObject2.put("employee", employeeDetails2);
         
        //Add employees to list
        JSONArray employeeList = new JSONArray();
        employeeList.add(employeeObject);
        employeeList.add(employeeObject2);
         
        //Write JSON file
        try (FileWriter file = new FileWriter("employees.json")) {
            file.write(employeeList.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
    private static void parseEmployeeObject(JSONObject employee) {
        //Get employee object within list
        JSONObject employeeObject = (JSONObject) employee.get("employee");
         
        //Get employee first name
        String firstName = (String) employeeObject.get("firstName");    
        System.out.println(firstName);
         
        //Get employee last name
        String lastName = (String) employeeObject.get("lastName");  
        System.out.println(lastName);
         
        //Get employee website name
        String website = (String) employeeObject.get("website");    
        System.out.println(website);
    }
    public void getJSONFile() {
    	//JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
         
        try(FileReader reader = new FileReader("employees.json")) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            // Object를 리턴하므로 단순 객체인경우 JSONObject나 배열인 경우 JSONArray로 캐스팅
            JSONObject jsonObject = (JSONObject) obj;

            //String name = (String) jsonObject.get("name"); 
            //System.out.println("name :: " +name); 
            //long id = (Long) jsonObject.get("id"); 
            //System.out.println("id :: " + id);
            
            // Array 
            /*JSONArray phoneNum = (JSONArray) jsonObject.get("phoneNumbers"); 
            Iterator<String> iterator = phoneNum.iterator(); 
            while (iterator.hasNext()) { 
            	System.out.println("phoneNumbers :: " + iterator.next()); 
            }*/

            // Object 
            /*JSONArray array = (JSONArray) jsonObject.get("address"); 
            for(int i=0; i<array.size(); i++){ 
            	JSONObject result = (JSONObject) array.get(i); 
            	System.out.println("result :: " +result.get("zipcode")); 
            }*/

            //
            JSONArray employeeList = (JSONArray) obj;
            System.out.println(employeeList);
             
            //Iterate over employee array
            //employeeList.forEach( emp -> parseEmployeeObject( (JSONObject) emp ) );
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }catch(ParseException e) {
            e.printStackTrace();
        }
    }
    
    // doTag()를 오버라이딩 하여 커스텀 태그 처리 내용을 개발한다.
    @Override
    public void doTag() throws JspException, IOException {
        // JspContext는 이 태그를 사용한 JSP 페이지에 대한 정보를  담고 있다.  
        PageContext context = (PageContext)this.getJspContext();
        
        // 태그가 호출된 JSP에 대해 요청 정보를 가져온다.
        HttpServletRequest request =  (HttpServletRequest)context.getRequest();
        System.out.println("태그가 호출된 JSP URI : " +  request.getRequestURI());
        
        // 태그를 사용할때 지정한 속성값들
        // getter / setter
        // 태그의 속성을 처리하기 위한 것으로 java bean 규약에 맞게 메서드 규칙을 지켜주어야 합니다.
        // color="blueviolet" iterNum="5"
        String color = this.getColor();
        int iterNum = this.getIterNum();
        
        // 커스텀 태그의 몸체를 담을 StringWriter
        StringWriter stringWriter = new StringWriter();
        
        // invoke()가 실행되면 StringWriter의 몸체 텍스트가 담긴다.
        getJspBody().invoke(stringWriter);
        String bodyText = stringWriter.toString();      
        
        // 프로퍼티 접근 
        // Java Web 에서 필요한 Front 리소스 정보(바라봐야할 빌드정보)는 젠킨스에서 .properties 파일로 저장된다. 
        //Properties prop = getProperties("service.properties");
        Properties propStatic = getPropertiesStatic("service.properties"); 
        
        // clean install -DBUILD_NUMBER=${BUILD_NUMBER} -DBUILD_ID=${BUILD_ID} ... etc.
        //System.out.println("Build Number : " + System.getenv("BUILD_NUMBER"));
        
        // @PropertySource("classpath:setting.properties") 프로퍼티 내부 읽기
        //environment.getProperty("java.version");  
        
        /*
        CompositeConfiguration configuration = new CompositeConfiguration();
        try {
			configuration.addConfiguration(new SystemConfiguration());
			configuration.addConfiguration(new PropertiesConfiguration("app.properties"));
        }catch(ConfigurationException e) {
			e.printStackTrace();
        }
        */
        
        // Jsp 페이지의 결과를 브라우저로 출력할 출력스트림를 얻는다.
        // JSP의 out 객체와 같음.
        JspWriter out = context.getOut();
        
        //out.print(prop.getProperty("hello"));
        out.print("<div>hello : " + propStatic.getProperty("hello") + "</div>");
        out.print("<div>ACTIVE : " + propStatic.getProperty("ACTIVE") + "</div>");
        out.print("<div>FRONT_TEST : " + propStatic.getProperty("FRONT_TEST") + "</div>");
        out.print("<div>FRONT_STAGE : " + propStatic.getProperty("FRONT_STAGE") + "</div>");
        out.print("<div>FRONT_PRODUCTION : " + propStatic.getProperty("FRONT_PRODUCTION") + "</div>");
        out.print("<div>");
        out.print("<span style=\"background-color:"+ color + ";\">");
        for(int i=0; i < iterNum; i++ ) {
            //out으로 내용 출력
            out.print(bodyText);
        }
        out.print("</span>");
        out.print("</div>");
    }

	// getter / setter
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public int getIterNum() {
        return iterNum;
    }
    public void setIterNum(int iterNum) {
        this.iterNum = iterNum;
    }
} 