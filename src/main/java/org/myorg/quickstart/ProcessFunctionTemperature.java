/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.myorg.quickstart;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * Implements a streaming windowed version of the "WordCount" program.
 *
 * <p>This program connects to a server socket and reads strings from the socket.
 * The easiest way to try this out is to open a text server (at port 12345)
 * using the <i>netcat</i> tool via
 * <pre>
 * nc -l 12345 on Linux or nc -l -p 12345 on Windows
 * </pre>
 * and run this example with the hostname and the port as arguments.
 */
@SuppressWarnings("serial")
public class ProcessFunctionTemperature {

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// get input data by connecting to the socket
		DataStream<String> input = env.socketTextStream("127.0.0.1", 7000, "\n");

//		StreamTableEnvironment streamTableEnvironment = StreamTableEnvironment.create(env);
		// 如果 1 秒内重复出现了的单词 报警；
		DataStream<Temperature> windowCounts = (DataStream<Temperature>) input
				.flatMap(new FlatMapFunction<String, Temperature>() {
					@Override
					public void flatMap(String value, Collector<Temperature> out) {
						String[] array  = value.split(",");
						out.collect(new Temperature(array[0], Long.valueOf(array[1])));
					}
				});

		DataStream<Temperature> prcessDateStream = windowCounts
				.keyBy("name")
//				.timeWindow(Time.seconds(5))
				.process(new KeyedWordRepeat());

//				.reduce(new ReduceFunction<WordWithCount>() {
//					@Override
//					public WordWithCount reduce(WordWithCount a, WordWithCount b) {
//						return new WordWithCount(a.word, a.count + b.count);
//					}
//				});
		windowCounts.print("input");
		// print the results with a single thread, rather than in parallel
		prcessDateStream.print("outPut").setParallelism(1);

		env.execute("Socket Window WordCount");
	}
	// ------------------------------------------------------------------------
}

class KeyedWordRepeat extends KeyedProcessFunction<Tuple,Temperature,Temperature> {

	/** The state that is maintained by this process function */
	/** process function维持的状态 */
//	private ValueState<Temperature> lastName = getRuntimeContext().getState(new ValueStateDescriptor<>("preWord", Temperature.class));
//	private ValueState<Long> lastTime = getRuntimeContext().getState(new ValueStateDescriptor<>("latTime", Long.class));
	private ValueState<Temperature> lastName ;
	private ValueState<Long> lastTime ;

	@Override
	public void open(Configuration parameters) throws Exception {
		lastName = getRuntimeContext().getState(new ValueStateDescriptor<>("preWord", Temperature.class));
		lastTime = getRuntimeContext().getState(new ValueStateDescriptor<>("latTime", Long.class));
	}

	@Override
	public void processElement(Temperature wordWithCount, Context context, Collector<Temperature> collector) throws Exception {

		// 获取当前的count
		Temperature preWord = lastName.value();

		lastName.update(wordWithCount);
		//第一次进入的时候
		if(lastName.value() == null || lastTime.value() == null){
			lastTime.update(1l);
			return;
		}
		Long lastTimeStamp = lastTime.value();
		//如果是同一个人，体温上升了
		if(preWord.name.equals(wordWithCount.name) && preWord.temperature<wordWithCount.temperature){
			long timeStamp = context.timerService().currentProcessingTime();
			collector.collect(wordWithCount);
//			context.timerService().registerProcessingTimeTimer(timeStamp+10000l); // 配合下方定时回调用
			lastTime.update(timeStamp);
			//如果不满足 同一个人体温上升，清除定时器
		}else if(!(preWord.name.equals(wordWithCount.name) && preWord.temperature<wordWithCount.temperature)
				|| lastTimeStamp == null ||lastTimeStamp == 1l){
//			context.timerService().deleteEventTimeTimer(lastTimeStamp); // 配合下方定时回调用
			lastTime.clear();
		}
	}
//
//连续5 秒后，温度上升
//	@Override
//	public void onTimer(long timestamp, OnTimerContext ctx, Collector<Temperature> out) throws Exception {
//		out.collect(lastName.value());
//		lastTime.clear();
//	}

}