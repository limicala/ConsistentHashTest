package demo.pojo;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("1");
        ChannelFuture f = ctx.write(new UnixTime());

        ctx.flush();

        f.addListener(ChannelFutureListener.CLOSE);
    }
}
