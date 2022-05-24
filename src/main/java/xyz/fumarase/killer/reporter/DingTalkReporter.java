package xyz.fumarase.killer.reporter;

import com.github.jaemon.dinger.DingerSender;
import com.github.jaemon.dinger.core.entity.DingerRequest;
import com.github.jaemon.dinger.core.entity.enums.MessageSubType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DingTalkReporter implements Reporter {

    private DingerSender dingerSender;

    @Autowired
    public void setDingerSender(DingerSender dingerSender) {
        this.dingerSender = dingerSender;
    }

    @Override
    public void report(String msg) {
        dingerSender.send(
                MessageSubType.TEXT,
                DingerRequest.request(msg, "状态通报")
        );
    }
}
