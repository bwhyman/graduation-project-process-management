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
  "name": "开题答辩",
  "auth": "zg0NS",
  "items": "[{\"number\": 0, \"name\": \"选题依据\", \"point\": 50}, {\"number\": 1, \"name\": \"设计方案\", \"point\": 25}, {\"number\": 2, \"name\": \"答辩过程\", \"point\": 25}]"
}
```

```json
[{"number": 0, "name": "选题依据", "point": 50}, {"number": 1, "name": "设计方案", "point": 25}, {"number": 2, "name": "答辩过程", "point": 25}]
```

```json
{
  "name": "期中检查",
  "auth": "AsImV",
  "items": "[{\"number\": 0, \"point\": 50, \"name\": \"软件工程\"},{\"number\": 1, \"point\": 50, \"name\": \"工程管理\"}]"
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
  "studentId": "1135242441437204480",
  "teacherId": "1135242464761729024",
  "processId": "1135247973229252608",
  "detail": "{\"teacherName\": \"李琰\", \"score\": 82, \"detail\": [{\"number\": 0, \"score\": 80}, {\"number\": 1, \"score\": 84}]}"
}
```

```json
{
  "teacherName": "李琰",
  "score": 82,
  "detail": [
    {
      "number": 0,
      "score": 80
    },
    {
      "number": 1,
      "score": 84
    }
  ]
}
```

比例

开题答辩：20；选题依据：50；设计方案：25；答辩过程：25
期中检查：20；软件工程：50；工程管理：50
毕业答辩：50；综合利用：10；相关知识：20；解决问题：10；完整详实：40；答辩过程：20
毕业演示：10；系统演示：100



