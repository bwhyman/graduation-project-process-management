# API

导入导师。teacher属性在前端直接序列化为字符串；声明组；

```json
[
  {
    "name": "BO",
    "number": "2001",
    "teacher": "{\"total\": 10, \"count\": 0}",
    "groupNumber": 1
  },
  {
    "name": "SUN",
    "number": "2002",
    "teacher": "{\"total\": 5, \"count\": 0}",
    "groupNumber": 2
  }
]
```

添加过程

```json
{
  "name": "开题",
  "auth": 2,
  "items": "[\n  {\n    \"number\": 0,\n    \"name\": \"2.1\",\n    \"point\": 50,\n    \"description\": \"选题依据（选题意义，国内外动态，初步设想及创新点等）及可行性论述。\"\n  },\n  {\n    \"number\": 1,\n    \"name\": \"3.3\",\n    \"point\": 50,\n    \"description\": \"开题答辩过程中能否清楚陈述自己对毕业设计题目的深入理解，表达思路是否清晰，重点是否突出，能否正确回答与毕设工作相关的提问，开题报告结构组织是否清晰合理、行文语言是否流畅准确、撰写格式是否符合规范要求。\"\n  }\n]"
}
```

```json
{
  "name": "期中",
  "auth": 1,
  "items": "[\n    {\n      \"number\": 0,\n      \"point\": 50,\n      \"name\": \"5.1\",\n      \"description\": \"期中-1\"\n    },\n    {\n      \"number\": 1,\n      \"point\": 50,\n      \"name\": \"5.5\",\n      \"description\": \"期中-2\"\n    }\n  ]"
}
```

添加学生

```json
[
  {
    "name": "wang-1",
    "number": "202001"

  },
  {
    "name": "wang-2",
    "number": "202002"
  },
  {
    "name": "zhang-1",
    "number": "202003"

  },
  {
    "name": "zhang-2",
    "number": "202004"
  }
]
```

导入题目

```json
[
  {
    "number": "202001",
    "projectTitle": "ProjectTitle-1"
  },
  {
    "number": "202002",
    "projectTitle": "ProjectTitle-2"
  },
  {
    "number": "202003",
    "projectTitle": "ProjectTitle-3"
  },
  {
    "number": "202004",
    "projectTitle": "ProjectTitle-4"
  }
```

指定过程指定学生，评分；

```json
{
  "processId": "1070382937616666624",
  "studentId": "1070387139730460672",
  "score" 80: 
}
```
