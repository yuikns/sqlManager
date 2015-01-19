sqlManager
==========

code for orgnization rank function

---

## usage

1. build index(usually for update)
buildLuceneIndex()
2. export to mongo
exportToMongo()

---

## overview
### api

warp for function in scr.core
scr.api.Usage.java

### core

api function for index-building, query, evaluate, etc. 

**buildAllMessIndex**

*readSql()*

1. **index directory**: ./indexDirSql
2. **index field**:id,author,jconf,year,firstAuthorOrg,secordAuthorOrg,thirdAuthorOrg,nCite,content
3. **notice**:
 - year: range from 2003-2013(now)
 - jconf: abbr. of 39 CCF A class conferences in uppercase
 - XXAuthorOrg: raw organization data extracted from paper or website
 - nCite: -1 means not found
 - content: all the things above 

**OrgMeta**

*exportData()*

before insert json data into mongo, it will print debug message every 20 orgnaization:

* res:1054	Loughborough University	Loughborough University	0
* line:10.00	3.00	3.59	40.00	6.49	0.88	6.95	0.00 ...
* 1040:{"idorg":"1054","org":"Loughborough University","orgClusterText":"Loughborough University","type":"0","typeScore":[{"count":"10.00","countReg":"3.59","cite":"40.00","citeLog":"6.49","aminerScore":"0.88","firstAuthorNum":"3.00","aIndex":"6.95"} ...]}

*getOrgRankListNew()*



### service

provide basic service function

**OrgrankOrgService**

*queryMeta(String... type)*

SELECT `idorg`, `org`, `orgClusterText`, `type` FROM orgranktest.org WHERE type...

idorg(integer range from 1-1000+) eg. Tsinghua has idorg of 12
org(string) means standard form of organization name.
orgClusterText(string) several writing forms for a organiztion seperated by ';'
type(integer) means academic, industry, research center, or all 

**MongoService**

*insertOrgJsonIntoMongo(List<String> jsons)*

insert given jsons list into collection "orgranktest"
these jsons should contain "idorg","org","orgClusterText" and a list of different score

**TxtService**

*getStringList(String filePath, List<String> util)*

read inputStream(utf-8) from file and saved into a util by line

### model

provide some class defined for paper, org, etc


