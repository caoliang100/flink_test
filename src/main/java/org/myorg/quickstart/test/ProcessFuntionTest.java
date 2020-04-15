//package org.myorg.quickstart.test;
//
//import org.apache.flink.api.common.functions.FlatMapFunction;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.functions.ProcessFunction;
//import org.apache.flink.util.Collector;
//import org.myorg.quickstart.WordWithCount;
//
//
//public class ProcessFuntionTest{
//
//    public static void main(String[] args) throws Exception {
//        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
//        // get input data by connecting to the socket
//        DataStream<String> text = env.socketTextStream("127.0.0.1", 7000, "\n");
////        DataStream<Tuple2<String, String>> stream = ...;
//
//        // parse the data, group it, window it, and aggregate the counts
//        DataStream<WordWithCount> windowCounts = (DataStream<WordWithCount>) text
//                .flatMap(new FlatMapFunction<String, WordWithCount>() {
//                    @Override
//                    public void flatMap(String value, Collector<WordWithCount> out) {
//                        for (String word : value.split("\\s")) {
//                            out.collect(new WordWithCount(word, 1L));
//                        }
//                    }
//                })
//                .process(new WordRepeat());
//
////                .keyBy("word")
////				.timeWindow(Time.seconds(5))
////                .process(new WordRepeat());
////				.reduce(new ReduceFunction<WordWithCount>() {
////					@Override
////					public WordWithCount reduce(WordWithCount a, WordWithCount b) {
////						return new WordWithCount(a.word, a.count + b.count);
////					}
////				});
//
//        // print the results with a single thread, rather than in parallel
//        windowCounts.print().setParallelism(1);
//
//        env.execute("Socket Window WordCount");
////
////// 将 process function 应用到一个键控流(keyed stream)中
////        DataStream<Tuple2<String, Long>> result = stream
////                .keyBy(0)
////                .process(new CountWithTimeoutFunction());
//    }
//}
///**
// * The data type stored in the state
// * state中保存的数据类型
// */
//class CountWithTimestamp {
//
//    public String key;
//    public long count;
//    public long lastModified;
//}
//
///**
// * The implementation of the ProcessFunction that maintains the count and timeouts
// * ProcessFunction的实现，用来维护计数和超时
// */
//class WordRepeat extends ProcessFunction<WordWithCount,WordWithCount> {
//    @Override
//    public void processElement(WordWithCount wordWithCount, Context context, Collector<WordWithCount> collector) throws Exception {
//
//    }
//}