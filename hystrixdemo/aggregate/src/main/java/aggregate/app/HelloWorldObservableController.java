package aggregate.app;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import aggregate.commands.obs.HelloWorldObservableCommand;
import aggregate.commands.simple.HelloWorldCommand;
import com.netflix.governator.annotations.AutoBindSingleton;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

@AutoBindSingleton
public class HelloWorldObservableController implements RequestHandler<ByteBuf, ByteBuf> {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldObservableController.class);

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        return request.getContent()
                .map(c -> request.getQueryParameters().get("greeting").get(0))
                .flatMap(message -> {
                    logger.info("About to execute HelloWorld command");
                    return new HelloWorldObservableCommand(message).observe();
                })
                .flatMap(str -> {
                            try {
                                return response.writeStringAndFlush(str);
                            } catch (Exception e) {
                                response.setStatus(HttpResponseStatus.BAD_REQUEST);
                                return response.close();
                            }
                        }
                );
    }

    public static void main(String[] args) {
        List<String> names =new ArrayList<String>();
        names.add("Jack");
        Predicate<String> startsWithJ = (n) -> n.startsWith("J");
        Predicate<String> fourLetterLong = (n) -> n.length() == 4;
        names.stream()
                .filter(startsWithJ.and(fourLetterLong))
                .forEach((n) -> System.out.print("nName, which starts with 'J' and four letter long is : " + n));

    }
}
