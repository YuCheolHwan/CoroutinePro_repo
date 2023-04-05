package com.example.coroutinepro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.example.coroutinepro.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var handler: Handler
    lateinit var channel : Channel<Long>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 1. 핸들러 등록
        handler = object : Handler(){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                binding.tvSumResult.text = "합계 출력 : ${msg.obj}"
            }
        }

        // 2. 코루틴 처리방식
        // 2-1. 메세지 역할 채널 객체 생성
        channel = Channel<Long>()
        // 2-2. Handler 역할
        val mainScope = GlobalScope.launch(Dispatchers.Main){
            channel.consumeEach {
                binding.tvSumResult.text = "합계 출력 : ${it}"
            }
        }

        // 3. 스레드를 설계한다.
        val backGroundScope = CoroutineScope(Dispatchers.Default + Job())

        binding.btnClick.setOnClickListener {
            backGroundScope.launch {
                var sum = 0L
                var time = measureTimeMillis {
                    for (i in 0..2_000_000_000) {
                        sum += i
                    }
                }
                Log.e("MainActivity", "${time}초 걸림")
//            binding.tvSumResult.text = "합계 출력 : ${sum}"
//                val message = Message()
//                message.obj = "${sum}"
                channel.send(sum.toLong())
            }

        }
        binding.btnReset.setOnClickListener {
            binding.tvSumResult.text = "합계 출력 : 0"
        }


        // 버튼을 클릭했을 때 오랫동안(6~8초) 시간이 걸리는 작업을 요청
//        binding.btnClick.setOnClickListener {
//            thread {
//            var sum = 0L
//            var time = measureTimeMillis {
//                for (i in 0..2_000_000_000) {
//                    sum += i
//                }
//            }
//            Log.e("MainActivity", "${time}초 걸림")
////            binding.tvSumResult.text = "합계 출력 : ${sum}"
//            val message = Message()
//                message.obj = "${sum}"
//                handler.sendMessage(message)
//            }
//
//        }
//        binding.btnReset.setOnClickListener {
//            binding.tvSumResult.text = "합계 출력 : 0"
//        }
    }

}