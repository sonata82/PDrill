/**
 * 
 */
package me.devcom.pdrill;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Furnace;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Remko Plantenga
 * 
 */
public class FuelManagerTest {

    private Mockery context = new Mockery();

    @Test
    public void testFuel() {
        final IDrill drill = this.context.mock(IDrill.class);
        final Map<Integer, Fuel> configuredFuels = new HashMap<Integer, Fuel>();
        configuredFuels.put(0, new Fuel(1, 1, 1, 1, 1, "test"));
        final Furnace emptyFurnace = this.context.mock(Furnace.class);

        this.context.checking(new Expectations() {
            {
                oneOf(drill).getConfiguredFuels();
                will(returnValue(configuredFuels));
                oneOf(drill).isVirtual();
                will(returnValue(false));
                oneOf(drill).getBlockState();
                will(returnValue(emptyFurnace));
                oneOf(emptyFurnace).getInventory();

            }
        });
        FuelManager fm = new FuelManager(drill);
        fm.fuel();
    }

}
