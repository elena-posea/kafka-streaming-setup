import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.{HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.{Milliseconds, StreamingContext}

object KafkaStarter extends LazyLogging {
  def main(args: Array[String]): Unit = {
    val session = SparkSession.builder.master("local[2]").appName("My app").getOrCreate
    val ssc = new StreamingContext(session.sparkContext, Milliseconds(10000)) // 10s
    val dstream = makeDStream(ssc)
    dstream.foreachRDD {
      consumerRecord => {
        if (!consumerRecord.isEmpty()) {
          // val offsets = consumerRecord.asInstanceOf[HasOffsetRanges].offsetRanges
          doSomething(consumerRecord)
          // write offsets if you are persisting them anywhere
          logger.info("NOT persisting offsets")
        }
      }
    }
    ssc.start()
    try {
      ssc.awaitTermination()
    } catch {
      case e: Exception =>
      //System.out.println("Error closing the context")
    }
  }

  def doSomething(rdd: RDD[(String, String)]) = {
    // noop
    logger.info("Doing something")
//    rdd.foreach {
//      case (key, codedMsg: String) => {
//        val msg = codedMsg
//        logger.info(msg)
//      }
//    }

    rdd.foreachPartition(
      (it: Iterator[(String, String)]) => {
        while(it.hasNext) {
          val record: (String, String) = it.next()
          System.out.printf("key = %s, value = %s\n", record._1, new String(record._2));
        }
      }
    )
  }

  private def makeDStream(ssc: StreamingContext): DStream[(String, String)] = {
    val topic = "elena"
    val brokers = "localhost:9092"
    val beginOffset = "smallest"
    val consumer = "myconsumergroup"
    val zkConnect = "localhost:2181"

    val map: Map[String, String] = Map(
      "bootstrap.servers" -> brokers,
      "auto.offset.reset" -> beginOffset,
      "group.id" -> consumer,
      "zookeeper.connect" -> zkConnect
    )
    // start with fresh offsets
    KafkaUtils.createDirectStream[String, String, NoopStringDecoder, NoopStringDecoder](ssc, map, Set(topic))
  }
}