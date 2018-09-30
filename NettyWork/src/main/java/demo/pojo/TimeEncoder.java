package demo.pojo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class TimeEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        UnixTime time = (UnixTime) msg;

        ByteBuf buf = ctx.alloc().buffer(4);

        buf.writeInt((int) time.value());

        System.out.println("2");

        // what is the difference between the couple of write functions
        //ctx.write(buf);
        ctx.write(buf, promise);
    }
}
