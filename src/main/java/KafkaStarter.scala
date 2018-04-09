import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.{HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.{Milliseconds, StreamingContext}

object KafkaStarter extends LazyLogging {
  def main(args: Array[String]): Unit = {
    val session = SparkSession.builder.master("local[2]").appName("My app").getOrCreate
    val ssc = new StreamingContext(session.sparkContext, Milliseconds(30000)) // 30s
    val dstream = makeDStream(ssc)
    dstream.foreachRDD {
      rdd => {
        if (!rdd.isEmpty()) {
          val offsets = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
          doSomething(rdd)
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
    rdd.foreach {
      case (_: String, codedMsg: String) => {
        val msg = codedMsg
        logger.info(msg)
      }
    }
    // vs rdd.foreachPartition()
  }

  private def makeDStream(ssc: StreamingContext): DStream[(String, String)] = {
    val topic = "elena"
    val brokers = "localhost:9092"
    val beginOffset = "largest"
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