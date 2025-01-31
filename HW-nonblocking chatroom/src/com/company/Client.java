package com.company;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    public static void main(String[] args) throws Exception {
        Selector selector =Selector.open();

        SocketChannel clientch = SocketChannel.open();
        clientch.configureBlocking(false);
        clientch.register(selector, SelectionKey.OP_CONNECT);

        clientch.connect(new InetSocketAddress("127.0.0.1", 9000));

        while (true){
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();

            while (it.hasNext()){
                SelectionKey key = it.next();
              //  it.remove();
                //check or do something with event
//                System.out.println("Input Something: ");
//                String cmd = "Hi";
//                System.out.println(cmd);

                if(key.isConnectable()){
                    SocketChannel ch = (SocketChannel) key.channel();
//                    if(!ch.finishConnect()){
//                        ch.close();
//                        continue;
//                    }
                    //ch.configureBlocking(false);

                    if(ch.isConnectionPending()){
                        ch.finishConnect();
                        ByteBuffer buf = ByteBuffer.allocate(1000);
                        buf.put(("Connected...".getBytes()));
                        buf.flip();
                        ch.write(buf);

                        ExecutorService executorService = Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());
                        executorService.submit(() -> {
                            while(true) {
                                buf.clear();
                                Scanner sc = new Scanner(System.in);
                                System.out.println("Input: ");
                                String msg = sc.nextLine();
                                buf.put(msg.getBytes());
                                buf.flip();
                                ch.write(buf);
                            }
                        });
                    }
                    ch.register(selector, SelectionKey.OP_READ);
                }
//                if(key.isWritable()){
//                    System.out.println("Input Something: ");
//                    String cmd = sc.nextLine();
//
//                    SocketChannel ch = (SocketChannel) key.channel();
//                    ByteBuffer buf = ByteBuffer.allocate(20);
//                    buf.put(cmd.getBytes());
//                    buf.flip();
//                    ch.write(buf);
//                    ch.register(selector, SelectionKey.OP_READ);
//                }

                if(key.isReadable()){
                    SocketChannel ch = (SocketChannel) key.channel();
                    ByteBuffer buf = ByteBuffer.allocate(3000);
                    ch.read(buf);
                    buf.flip();
                    String serverMsg = new String(buf.array());
                    System.out.println(serverMsg);
                }
                it.remove();
            }
        }

    }
}
