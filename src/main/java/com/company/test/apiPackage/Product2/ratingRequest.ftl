{
  "Policy": {
	<#list Policy as result>"${result.atrib}":"${result.value}", 
	</#list>
  	"HiredAutoAndNonOwnedAutoLiability": {
	 <#list HiredAutoAndNonOwnedAutoLiability as result>"${result.atrib}":"${result.value}"<#if result?is_last><#else>,</#if>
	 </#list> 
    },
  	"ChangesLimitedFungiCoverage": {
      "SeparatePremisesLocationsOption": "No",
      "BOPLimitedFungiBacteriaCovBusnIncomeExtraExpenseCoverageNumDays": "",
      "Limit": "15000"
    },
  	"Location": [
      {
        "LocationDetail": {
		<#list LocationDetail as result>"${result.atrib}":"${result.value}",
		</#list>
  		"WaterBackUpAndSumpOverflow": {
            "CoveredPropertyAnnualAggregateLimit": "5000",
            "BusinessIncomeExtraExpenseAnnualAggregateLimit": "5000",
            "Limit": "5000"
        },
  		"Building": [
            {
              "BuildingDetail": {
			  <#list BuildingDetail as result>"${result.atrib}":"${result.value}",
			   </#list>
  				"DebrisRemovalAdditionalInsurance": {
                  "Limit": "10000"
                },
  				"Classification": [
                  {
                    "ClassificationDetail": {
					<#list ClassificationDetail as result>"${result.atrib}":"${result.value}"<#if result?is_last><#else>,</#if>
					</#list>      
  					}
                  }
                ]
              }
            }
          ]
        }
      }
    ]
  },
  "PaaSClientIPAddress": "125.25.25.2",
  "EndClientUserBrowserInformation": "Mozilla/...",
  "OwnerId": "24",
  "ServiceRequestDetail": {
  <#list ServiceRequestDetail as result>"${result.atrib}":"${result.value}"<#if result?is_last><#else>,</#if>
  </#list>      
  },
  "EndClientUserUniqueSessionId": "Uniquesession",
  "EndClientUserBrowserSessionId": "7Eadfad4444",
  "ClientUniqueRequestID": "1"
}