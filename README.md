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

*countOrgNew(String... type)*

type here is a list of conference name

read from lucene index and compute the score. By the way, it will save compute result in ./ as scoreX.txt
double[] sum saves 7 scores for a given organization
0. #paper
1. #paper where the first author belongs to this organization
2. normalized #paper according to author order k(\frac{1}{k}\sum_{i=1}^n{\frac{1}{i}})
3. sum of #citations 
4. AMiner-score
5. a better AMiner-score, which normalized by year
6. A-index (a diffent way of normalize contribution based on author order)

*orgQueryYear(int seq, String orgs, int year, String[]... type)*

seq(range 0-4) 1 means 1st-author field, 2 means 2nd ...0 means all;
orgs should be 'orgClusterText'

translate parameters into a lucene query, compute result based on records found, and return a 4-double array
which means #records-found,#sum(n_cites in records),#sum(lg(n_cites+10) in records),A-score(records)
these can be the baseline for higher level score computation function(such as countOrg(),countOrgNew())

**OrgMeta**

*exportData()*

before insert json data into mongo, it will print debug message every 20 orgnaization:

* res:1054	Loughborough University	Loughborough University	0
* line:10.00	3.00	3.59	40.00	6.49	0.88	6.95	0.00 ...
* 1040:{"idorg":"1054","org":"Loughborough University","orgClusterText":"Loughborough University","type":"0","typeScore":[{"count":"10.00","countReg":"3.59","cite":"40.00","citeLog":"6.49","aminerScore":"0.88","firstAuthorNum":"3.00","aIndex":"6.95"} ...]}

*getOrgRankListNew(int n)*

n(range from 0 to 10) is #table 


### service

provide basic service function

**OrgrankOrgService**

sql function share common return type as list of string which col-elems seperated by '\t'

*queryMeta(String... type)*

SELECT `idorg`, `org`, `orgClusterText`, `type` FROM orgranktest.org WHERE type...

-idorg(integer range from 1-1000+) eg. Tsinghua has idorg of 12
-org(string) means standard form of organization name.
-orgClusterText(string) several writing forms for a organiztion seperated by ';'
-type(integer) means academic, industry, research center, or all 

*queryMetaById((String Id)*

SELECT `idorg`, `org`, `orgClusterText` FROM orgranktest.org WHERE `idorg`=...

*queryScore(String isAcademic,String sortOrder)*

SELECT `idOrg` FROM orgranktest.org WHERE type=... ORDER BY ...

**MongoService**

*insertOrgJsonIntoMongo(List<String> jsons)*

insert given jsons list into collection "orgranktest"
these jsons should contain `idorg`,`org`,`orgClusterText` and a list of different score

**TxtService**

*getStringList(String filePath, List<String> util)*

read inputStream(utf-8) from file and saved into a util by line

### model

provide some class defined for paper, org, etc


