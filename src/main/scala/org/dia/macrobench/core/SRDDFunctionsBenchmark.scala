/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dia.macrobench.core

import java.util.concurrent.TimeUnit

import org.apache.spark.rdd.RDD
import org.openjdk.jmh.annotations._

import org.dia.core.{SciDataset, SciSparkContext, Variable}
import org.dia.core.SRDDFunctions._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
class SRDDFunctionsBenchmark {

  @Param(Array("1gb/", "10gb/", "100gb/", "1000gb/"))
  var directory : String = _

  var sc : SciSparkContext = BenchmarkContext.sc
  var fspath : String = BenchmarkContext.fspath

  var srdd : RDD[SciDataset] = _

  @Setup
  def setup() : Unit = {
    srdd = sc.sciDatasets(fspath + directory, List("ch4"))
      .map(p => p("FRAME") = p.datasetName.split("_")(1))
  }

  @TearDown
  def teardown() : Unit = sc.sparkContext.stop()

  @Benchmark
  def repartitionBySpace(): Long = {
    srdd.repartitionBySpace("ch4", p => p.attr("FRAME").toInt, 20, 20).count
  }
}
