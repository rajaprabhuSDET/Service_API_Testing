{
  "ServiceRequestDetail": {
    <#list ServiceRequestDetail as result>"${result.atrib}":"${result.value}"<#if result?is_last><#else>,</#if>
  </#list>
  },
  "AttributeListDetail": {
    "AttributeDetail": [
    <#list PolicyInformation as result>
	    {
	    	"Key" : "${result.atrib}",
	    	"Value" : "${result.value}"
	    }<#if result?is_last><#else>,</#if>
	  </#list>
     ]
  }
}