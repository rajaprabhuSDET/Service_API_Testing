{
  "Policy": {
   "AdditionalInsuredList": [ <#assign j=0>
	     <#list AdditionalInsuredType as result>{
	        "${result.atrib}":"${result.value}",
	        "AdditionalInsuredDetail": [ <#assign j=j+1>
	        <#list 1..array[i] as x>
	          {<#assign AdditionalInsuredDetails="AdditionalInsuredDetail"+j>
	          <#list AdditionalInsuredDetails?eval  as result>"${result.atrib}":"${result.value}"<#if result?is_last><#else>,</#if>
	          </#list>
	          }<#if x?is_last><#else>,</#if>
	          </#list>
	        ]<#assign i=i+1>
	      }<#if result?is_last><#else>,</#if>
	      </#list>
    ],
  "Location": {
      "LocationDetail": [
        {        
        "Classification": {
            "ClassificationDetail": [
              {
              <#list Classification as result>"${result.atrib}":"${result.value}"<#if result?is_last><#else>,</#if>
              </#list>
              }
            ]
          },
        <#list Location as result>"${result.atrib}":"${result.value}"<#if result?is_last><#else>,</#if>
        </#list>
        }
        ]
      },
  <#list Policy as result>"${result.atrib}":"${result.value}"<#if result?is_last><#else>,</#if>
  </#list>
   },
   "OwnerId": "30",
   "ServiceRequestDetail": {
   <#list ServiceRequestDetail as result>"${result.atrib}":"${result.value}"<#if result?is_last><#else>,</#if>
  </#list>
  },
  "EndClientUserUniqueSessionId": "Uniquesession"
}