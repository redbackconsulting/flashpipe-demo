import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import groovy.util.XmlSlurper;
import java.util.regex.*;
import groovy.xml.*;

def Message processData(Message message)
{   
    def body = message.getBody(java.lang.String) as String;
    def messageLog = messageLogFactory.getMessageLog(message);
    if(messageLog != null)
    {
        messageLog.setStringProperty("log1","Printing Payload As Attachment")
        messageLog.addAttachmentAsString("log1",body,"text/plain");
    }
    return message;
}

def Message logHeaders(Message message) {
	def headers = message.getHeaders()
	def messageLog = messageLogFactory.getMessageLog(message)
	for (header in headers) {
	   messageLog.setStringProperty("header." + header.getKey().toString(), header.getValue().toString())
	   //strHead = header.getValue().toString();
	   //strHead.replaceAll(/[^\x20-\x7E]+/g, '');
	  //messageLog.setStringProperty("headerX." + header.getKey().toString(), strHead);
	}
    return message;
}

def Message setTargetUri(Message message)
{   
    def sEntity = message.getProperty("sEntity") as String;
    switch (sEntity){
        case "OrgUnit":
        message.setProperty("SnowApiServiceUri", message.getProperty("SnowApiOrgChartUri"));
        break;
        case "CompanyCode":
        message.setProperty("SnowApiServiceUri", message.getProperty("SnowApiCompanyCodeUri"));            
        break;
    }
    return message;
}

def Message setImportSetTargetUri(Message msg)
{   
//    def body = msg.getBody(java.lang.String);
//    def xml = new XmlParser().parseText(body);
    def body = msg.getBody(java.io.Reader);
    def xml = new XmlSlurper().parse(body);
    //def entryId = xml.'**'.findAll { node -> node.name() == 'message' }*.@id   
    def entryId = msg.getProperty("SapDataStoreId");
//    println entryId[0];
    def import_set_id = xml.'**'.findAll { node -> node.name() == 'import_set_id' }*.text();
//    println import_set_id[0];
    def multi_import_set_id = xml.'**'.findAll { node -> node.name() == 'multi_import_set_id' }*.text();
//    println multi_import_set_id[0];
    //println "EntyrID" entryId;
//    def strEntryId = entryId[0] as String
    def strEntryId = entryId.toString();
    List<String> entry = Arrays.asList(strEntryId.split("_")); 
    msg.setProperty("EntryId", entryId[0]);
    msg.setProperty("sEntity", entry[0]);  
    msg.setProperty("ImportSetId", import_set_id[0]);
    msg.setProperty("MultipImportSetId", multi_import_set_id[0]);
    
    //println XmlUtil.serialize(xml);

        return msg;
    return message;
}

def Message logPayload(Message message)
{   
    def body = message.getBody(java.lang.String) as String;
    def messageLog = messageLogFactory.getMessageLog(message);
    if(messageLog != null)
    {
        messageLog.setStringProperty("log1","Printing Payload As Attachment")
        messageLog.addAttachmentAsString("log1",body,"text/plain");
    }
    return message;
}

def Message grabErrorResponse(Message message) {
                
    // get a map of properties
    def map = message.getProperties();
                
    // get an exception java class instance
    def ex = map.get("CamelExceptionCaught");
    if (ex!=null) {
                                
        // an http adapter throws an instance of org.apache.camel.component.ahc.AhcOperationFailedException
        if (ex.getClass().getCanonicalName().equals("org.apache.camel.component.ahc.AhcOperationFailedException")) {
                                        
            // save the http error response as a message attachment 
                def messageLog = messageLogFactory.getMessageLog(message);
                messageLog.addAttachmentAsString("http.Exception.ResponseBody", ex.getResponseBody(), "text/plain");

            // copy the http error response to an exchange property
                message.setProperty("http.Exception.ResponseBody",ex.getResponseBody());

            // copy the http error response to the message body
                message.setBody(ex.getResponseBody());

            // copy the value of http error code (i.e. 500) to a property
                message.setProperty("http.Exception.StatusCode",ex.getStatusCode());

            // copy the value of http error text (i.e. "Internal Server Error") to a property
                message.setProperty("http.Exception.StatusText",ex.getStatusText());
                                        
        }
    }
    return message;
}