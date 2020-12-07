package practice

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author tiankx
 * @date 2020/11/18 21:01
 * @version 1.0.0
 */
/*
1.	数据结构：时间戳，省份，城市，用户，广告，字段使用空格分割。
1516609143867 6 7 64 16
1516609143869 9 4 75 18
1516609143869 1 7 87 12
2.	需求: 统计出每一个省份广告被点击次数的 TOP3
3.
        ...
    => RDD[((province, ads), 1)] reduceByKey
    => RDD[((province, ads), count)] map
    => RDD[(province, (ads, count))] groupByKey
    => RDD[(province, List[(ads, count)])]
 */
object Practice1 {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Practice1").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val line = sc.textFile("files/province.text")
    val result = line.map(line => {
      val words = line.split("\\W+")
      ((words(1), words(4)), 1)
    }).reduceByKey(_ + _).map({ case ((province, ads), count) => (province, (ads, count)) })
      .groupByKey().map({ case (province, listIt) => (province, listIt.toList.sortBy(-_._2).take(3)) })
      .sortByKey()
    result.collect.foreach(println)
    sc.stop()
  }
}
