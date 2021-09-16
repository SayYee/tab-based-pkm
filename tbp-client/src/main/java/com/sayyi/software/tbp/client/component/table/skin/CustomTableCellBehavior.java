package com.sayyi.software.tbp.client.component.table.skin;

import com.sayyi.software.tbp.client.component.util.Scheduler;
import com.sun.javafx.scene.control.behavior.TableCellBehavior;
import javafx.concurrent.Task;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseButton;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的TableCell行为，希望双击的时候执行其他的操作，而不是触发编辑
 * @param <S>
 * @param <T>
 */
public class CustomTableCellBehavior<S, T> extends TableCellBehavior<S,T> {

    private ScheduledFuture<?> future;
    /** 单元格变为可编辑状态的延时时间。200ms操作上感觉可以接受 */
    private final long delayMills = 200;

    public CustomTableCellBehavior(TableCell<S, T> control) {
        super(control);
    }

    protected void handleClicks(MouseButton button, int clickCount, boolean isAlreadySelected) {
        // handle editing, which only occurs with the primary mouse button
        if (button == MouseButton.PRIMARY) {
            // 还有一种情况，选中再双击的处理
            // 选中时双击会出现： 1 true和2 true，还是会进入第一个分支。这个暂时先不做处理吧，后续想到了再说吧
            // 网上对这个问题的处理，基本都是自己接管双击判断。第一次点击到来后，延时执行任务，如果在延时内收到了双击，就取消单击任务，执行双击
            // 这么处理的问题在于，本质上是自己设定了双击间隔，而不是使用操作系统的双击设定。我不知道如何获取系统的双击时间间隔，暂且设定200ms的延时执行好了
            if (clickCount == 1 && isAlreadySelected) {
                future = Scheduler.get().schedule(genEditTask(), delayMills, TimeUnit.MILLISECONDS);
            } else if (clickCount == 1) {
                // cancel editing
                edit(null);
            } else if (clickCount == 2 && getNode().isEditable()) {
                // 双击不再触发编辑，而是取消编辑指令
                if (future != null) {
                    future.cancel(true);
                }
            }
        }
    }

    private Task<Void> genEditTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                return null;
            }
            @Override
            protected void succeeded() {
                edit(getNode());
            }
        };
    }
}
