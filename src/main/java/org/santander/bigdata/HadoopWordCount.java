package org.santander.bigdata;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

// Clase principal
public class HadoopWordCount {
    // Definitmos el Map mediante esta clase estática
    public static class Map
            // Hereda las características básicas de MapReduce
            extends MapReduceBase
            // Implementa la interfaz Mapper con los tipos correspondientes
            implements Mapper<LongWritable, Text, Text, IntWritable> {

        // Declaramos una variable de clase de tipo IntWritable con valor 1
        private final static IntWritable one = new IntWritable(1);
        // Declaramos una variable de tipo Text
        private Text word = new Text();

        // Implementamos la función map
        public void map (
                LongWritable key, // Número real representando la clave
                Text value, // Objeto Text representando el valor
                OutputCollector<Text, IntWritable> output, // Colector de salidas
                Reporter reporter // Reportador
        ) throws IOException { // Puede lanzar excepciones de entrada/salida
            String line = value.toString(); // Convertimos el texto del valor a formato String
            StringTokenizer tokenizer = new StringTokenizer(line); // Creamos un tokenizador sobre el valor
            while (tokenizer.hasMoreTokens()) { // Mientras el tokenizador contiene tokens
                word.set(tokenizer.nextToken()); // Asignamos el valor del token a la variable word
                output.collect(word, one); // Colectamos la salida con clave word y valor 1
            }
        }
    }

    // Definitmos el Reduce mediante esta clase estática
    public static class Reduce
            // Hereda las características básicas de MapReduce
            extends MapReduceBase
            // Implementa la interfaz Mapper con los tipos correspondientes
            implements Reducer<Text, IntWritable, Text, IntWritable> {

        // Implementamos la función reduce
        public void reduce(
                Text key,
                Iterator<IntWritable> values,
                OutputCollector<Text, IntWritable> output,
                Reporter reporter
        ) throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();
            }
            output.collect(key, new IntWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {
        // Creamos un nuevo Job
        JobConf conf = new JobConf(HadoopWordCount.class);
        // Establecemos el nombre del Job a HadoopWordCount
        conf.setJobName("HadoopWordCount");

        // Establecemos el tipo de la clave de salida
        conf.setOutputKeyClass(Text.class);
        // Establecemos el tipo del valor de salida
        conf.setOutputValueClass(IntWritable.class);

        // Establecemos la clase del mapeador
        conf.setMapperClass(Map.class);
        // Establecemos la clase del combinador
        conf.setCombinerClass(Reduce.class);
        // Establecemos la clase del reductor
        conf.setReducerClass(Reduce.class);

        // Establecemos el formato de entrada
        conf.setInputFormat(TextInputFormat.class);
        // Establecemos el formato de salida
        conf.setOutputFormat(TextOutputFormat.class);

        // Establecemos las rutas de entrada
        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        // Establecemos las rutas de salida
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        // Ejecutamos el Job
        JobClient.runJob(conf);
    }
}