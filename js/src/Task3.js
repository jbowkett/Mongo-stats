// task 3
db.regions.aggregate([
  {
    $project : {
      _id : {
        country : "$country",
        region : "$region"
      },
      growth_2012:{ $subtract : ["$2012_pop", "$2011_pop"] },
      growth_2011:{ $subtract : ["$2011_pop", "$2010_pop"] },
      growth_2010:{ $subtract : ["$2010_pop", "$2009_pop"] },
      growth_2009:{ $subtract : ["$2009_pop", "$2008_pop"] }
    }
  },
  {
    $group : {
      _id : "$_id",
      populations: { $push: "$growth_2012"}
    }
  }
])

