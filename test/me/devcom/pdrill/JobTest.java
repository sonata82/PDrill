package me.devcom.pdrill;

import org.bukkit.block.Block;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Remko Plantenga
 * 
 */
public class JobTest {

    private Mockery context = new Mockery();

    @Test
    public void testProcess() {
        final IDrill drill = this.context.mock(IDrill.class);
        final Block block = this.context.mock(Block.class);
        this.context.checking(new Expectations() {
            {
                oneOf(drill).isVirtual();
                will(returnValue(false));
                oneOf(drill).getFuel();
                Fuel fuel = new Fuel(1, 2, 3, 4, 5, "test");
                will(returnValue(fuel));
                oneOf(drill).getNextBlock("f");
                will(returnValue(block));
                oneOf(block).getTypeId();
                will(returnValue(1));
            }
        });
        Job j = new Job(drill, "f", 1);
        j.process();
    }

    @Test
    public void testProcessWithEmptyFurnace() {
        final IDrill drill = this.context.mock(IDrill.class);
        this.context.checking(new Expectations() {
            {
                oneOf(drill).isVirtual();
                will(returnValue(false));
                oneOf(drill).getFuel();
                will(returnValue(null));
                oneOf(drill).getId();
                will(returnValue(1));
                oneOf(drill).sendMessage(with(any(String.class)));
                oneOf(drill).setEnabled(false);
            }
        });
        Job j = new Job(drill, "f", 5);
        j.process();
    }
}
