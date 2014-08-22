//change the dataset so the docs look something like:
{'country':england, 'region':london, '2012':49141671}
filter to get just 2012 and 2011 out for each region

put through the aggregation pipeline to get the difference by summing the two values for each region
then order by difference
then limit(2)
save to the db


// task 2
db.regions.aggregate([
  {
    $project : {
      _id : {
        country : "$country",
        region : "$region"
      },
      recent_growth:{ $subtract : ["$2012_pop", "$2011_pop"] }
    }
  },
  {$sort : {"recent_growth" : -1}},
  {$limit : 2}
])

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

