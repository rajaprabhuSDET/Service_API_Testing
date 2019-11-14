{
  "ServiceRequestDetail": {
    "BrowserIp": "122.183.228.146:32342",
    "LanguageCode": "en",
    "ServiceRequestVersion": "1.0",
    "ServiceResponseVersion": "1.0",
    "OwnerId": "1",
    "ResponseType": "JSON",
    "Token": "cgHkrayYxCQUELzjteEsxZP32zou6UWFXKryWyV7StxpCXrv4Wrw5AgstHJauqCYY6mOb2twS7lE2gLBC2rvmCWYkLh+FaRheLYmZUkxg6ibLnNMvyfKBnAPWmjIgVotjrowQ2WIFXXZvUvpXSfBMeT9awPiKNSFioSpwNBEHOwmaxSb3mEPhv5rXZeY0sSsXL5fS2TeW+dPGnOmj4SeO7Kd5N6HI5kTDFaGp93ZHKImS0ua9+1dhH2a2NFE3jTF26+3hG+aUtGtRXqyXMaikBQfaWxzWWVCnSgfRfe8kvvNk5ojmshd+4Tw9HcbFtoLkhGXVwE9qunVWhUsVZ7pi14bVayR+x4q2N3FyZQvtT17+rxu+qr4hCPy4Xj4Oj7yAxAgHVVvCaJsWQY1rYyyxnZ9H0d6TNRjxTXoiDoiiGIPK1cpoZ5S+vDste8iAPxN+8HekhhfzTS75ePRV7ghTjsgEdhp6Qj8HoekYjdnpcY+Dw6SwFjgOlPR+E+oqS94NnBm24HhqUsHrwGRNmiu/Ak6/entff+jIEEwY+AZ8NXqZkgN+9gXeq2+g3CDGVDXySPNoxa1VGeWpv81drpmv26xhvKKr0n+bYryqFLn9QGudhGF4D1H7XcOG2zf29gJIYe0GuoleBMmCdhZn7QSgczLxFgibemrL8CIh1RqsDzEXbENDxEcL10M/YedsFH0YK60uPmpVtja5QJ5uGeoq6Mj9Y9dWaj/K1hsAK4spXHSlxNFFU1IpeEKHbGwyEEMjCFICuth3nRN4dzS/Rdh+S4lYoAtDjpJ6ss9J7vz5CVV0QEJZquSICUgtF3NSaEM3RMSAmhE2w6Dbx3iCKJex3PlEDwI2hVlOvleMjk6uzettcS4GqL0su2NawM7pEUBYDvphyZfG99mILy2irt9I4i6LNeyot3EaQ2JG7XdwYx6EyN7cCSruqAMrHdhC4pGXw/27HNlrG8XDi0yv1Lceoi6LNeyot3EaQ2JG7XdwYwMeL8PixrOjYaqFctR5OREzweH1g73446Q5vhwxU1mTbT73MHmgg7KZojulM6m9TLeXoLb834NTjV98O/GwbO+KDCvSHGkCMOJDuLOVwVS0putE15OSJ9xwRL8rwPs3oYTztXM/eH4swGroL0lOc4E1eQU9cmu8jicz+CZcFrSxz0Kmjr8hyzMmmU6zpTXrIl+CnfhWpD6+S9Yd3zUqr1ZMOJcos75m7Ei9AlemSt4mkK9R1F9Oy6FsRhDLZYl87Ane8XCir9ZRjMlHMlZM3Ad6EeYaXiPRiCG9dPhX07qqueDCvf5QdtLYThwTC07WQ30oWXpzPtaJrKlt9zSTDEMUeV52XrYOzXTnqC5qLqorIDRBynVCm7gA6cFd8o80dBcu2hCMAB/LvY1DfZn/skYAGMvQo5T2GTnIoOH4xzCZSo9kDkCoVjHSFQGwDNd88VLgwSGYG2ymfKGLu9bGsFg8S6it7DwsKd7PZk15OwTsOVyDRDOTQcQI34DqijTDF2FePZFpWRiFWy13I/eaO7DOuTgcbaViyvcVQWvS469JLcolI29pKZeTL5j3HLpt4pq6Mrh/hibRbws4qWi3vtJWI2PY6wAHD7B5Vk0P1/G28pPAMCjF0ATlW9uSyNBiotZIK+wjXKfNe6p9c4jCoeEHTNG3yERJbjRkk4IFOQmnqLHCCixhxgS9PHZAopovAAEYNly0lriMC/Zf/J3h2CSwfR9VETdzYxxyP8qlASyhaOJReoWqP2lEzml2e5SdILKraqHWG4rDToN3HzKrj3+M3BHNxcmMaRmQ862MiUexIyT/rFNG1RPO3RJ3IdGEFg5aACCA3yOOW0prtaqs8THpOyn+XBClz5tTcmPVukfDdD6QvRvZb5exJfI7Xh0xz1c37hsX29Dg1vNs95cAQVnaruShjAjCl3m1FLvruNgQw==",
    "UserName": "Agent"
  },
  "AttributeListDetail": {
    "AttributeDetail": [
      <#list AttributeDetail as result>
	    {
	    	"Key" : "${result.atrib}",
	    	"Value" : "${result.value}"
	    }<#if result?is_last><#else>,</#if>
</#list>
    ]
  }
}