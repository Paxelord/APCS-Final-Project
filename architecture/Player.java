package architecture;

import com.sun.javafx.geom.Point2D;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;


/**
 * The class of the main character, the protagonist, of the game. Player handles
 * inventory management, growth (leveling up), etc.
 *
 * @author Kevin Liu
 * @version May 8, 2017
 * @author Period: 5
 * @author Assignment: APCS Final
 *
 * @author Sources: none
 */
public class Player extends Combatant
{
    /**
     * The ratio between level-up requirements of level n and level n-1;
     */
    public static final double expGrowth = 1.5;

    /**
     * Level-up requirement of starting level
     */
    public static final int baseExp = 100;

    private int exp = 0, attributePoints = 0, expLimit;

    private List<Item> inventory = new LinkedList<Item>();

    // [Headgear, Chestpiece, Legpiece, Gloves, Shoes]
    private Armor[] equippedArmor = new Armor[5];

    private Weapon equippedWeapon;

    private static final int[] startingAttributes = { 14, 14, 9, 9, 7, 9, 4 };


    /**
     * Creates a default starting player.
     */
    public Player()
    {
        super();
        // Set starting level and attributes
        setLevel( 1 );
        setBaseAttributes( startingAttributes );
//        updateAttributes(); TODO: fix null pointer exceptions
        setExpLimit();

        // TODO Set starting equipment

    }

    public Player(Point2D startPose) {
        this();
        this.topLeftCorner = startPose;
    }

    /**
     * Adds exp to the Player and calls levelUp() if exp exceeds limit.
     * 
     * @param exp
     * @return true if levelUp() is called
     */
    public boolean gainExp( int exp )
    {
        this.exp += exp;
        if ( exp >= expLimit )
        {
            levelUp();
            return true;
        }
        return false;
    }


    /**
     * Sets the new exp limit after a level up, scaling exponentially with
     * level.
     */
    private void setExpLimit()
    {
        expLimit = (int)Math
            .round( baseExp * Math.pow( expGrowth, getLevel() - 1 ) );
    }


    /**
     * Called when player levels up; level is incremented, 5 attribute points
     * (AP) are added, and 1/2 of HP and MP is restored. Exp must exceed
     * expLimit.
     */
    public void levelUp()
    {
        setLevel( getLevel() + 1 );
        attributePoints += 5;
        restoreHealth( getStats()[0] / 2 );
        restoreMana( getStats()[1] / 2 );

        exp -= expLimit;
        setExpLimit();

        if ( exp >= expLimit )
            levelUp();

    }


    /**
     * Increases a baseAttribute by a single point at the cost of an attribute
     * point.
     * 
     * @param attribute
     *            index of the attribute
     */
    public void assignAttributePoint( int attribute )
    {
        if ( attribute < 0 || attribute >= 7 )
            throw new InvalidParameterException(
                "attribute index must fall between 0 and 6, inclusive" );

        if ( attributePoints > 0 )
        {
            getBaseAttributes()[attribute]++;
            attributePoints--;
            updateAttributes();
        }
    }


    @Override
    public void updateAttributes()
    {
        resetAttributes();
        // Add equippedWeapon boost
        for ( int i = 0; i < 7; i++ )
            getModifiedAttributes()[i] += equippedWeapon.getTotalBoosts()[i];

        // Add equippedArmor boosts
        for ( Armor armor : equippedArmor )
            for ( int i = 0; i < 7; i++ )
                getModifiedAttributes()[i] += armor.getTotalBoosts()[i];

        // Add ring boosts

        updateVolatileBoosts();
        updateStats();

    }


    @Override
    protected void updateStats()
    {

        super.updateStats();
        // TODO finish

        // ATK = (STR or INT) + MT

        // DEF = sumOf(defense of equippedArmor)

        // ACC = (DEX or WIS) * accuracyFactor + ACC(equippedWeapon)

    }


    /**
     * Equips a weapon from the inventory, simultaneously removing any
     * previously equipped weapon back to the inventory.
     * 
     * @param weapon
     *            weapon to equip
     */
    public void equip( Weapon weapon )
    {
        if ( inventory.contains( weapon ) )
        {
            unEquipWeapon();

            equippedWeapon = weapon;
            discard( weapon );

            updateAttributes();
        }
    }


    /**
     * Equips a piece of armor from the inventory, simultaneously removing any
     * conflicting armor back to the inventory.
     * 
     * @param armor
     *            armor to equip
     */
    public void equip( Armor armor )
    {
        if ( inventory.contains( armor ) )
        {
            unEquipArmor( armor.getType() );

            equippedArmor[armor.getType()] = armor;
            discard( armor );

            updateAttributes();
        }
    }


    /**
     * Equips a ring from the inventory, simultaneously removing any previously
     * equipped weapon back to the inventory if no space is available.
     * 
     * @param ring
     *            ring to equip
     */
    public void equip( Ring ring )
    {
        // TODO complete
    }


    /**
     * Returns a piece of equipped armor of type type back to the inventory.
     * 
     * @param type
     *            the slot/type of armor to remove
     */
    public void unEquipArmor( int type )
    {
        if ( equippedArmor[type] != null )
        {
            acquire( equippedArmor[type] );
            equippedArmor[type] = null;

            updateAttributes();
        }
    }


    /**
     * Returns the equipped weapon back to the inventory.
     */
    public void unEquipWeapon()
    {
        if ( equippedWeapon != null )
        {
            acquire( equippedWeapon );
            equippedWeapon = null;

            updateAttributes();
        }
    }


    /**
     * Returns an equipped ring back to the inventory
     * 
     * @param slot
     *            the ring slot from which to remove
     */
    public void unEquipRing( int slot )
    {
        // TODO complete
    }


    /**
     * Adds an item to the inventory of Player
     * 
     * @param item
     *            item to be added
     */
    public void acquire( Item item )
    {
        inventory.add( item );
    }


    /**
     * Adds a Collection of items to the inventory of Player
     * 
     * @param items
     *            items to be added
     */
    public void acquire( List<Item> items )
    {
        inventory.addAll( items );
    }


    /**
     * Removes an item from the inventory
     * 
     * @param item
     *            item to be removed
     * @return true if item removed, false if item not found
     */
    public boolean discard( Item item )
    {
        return inventory.remove( item );
    }


    /**
     * Uses a consumable in Player inventory
     * 
     * @param consumable
     *            consumable to be used
     * @return true if used, false if consumable not found
     */
    public boolean use( Consumable consumable )
    {

        if ( inventory.contains( consumable ) )
        {
            consumable.use( this );
            discard( consumable );
            return true;
        }
        return false;
    }


    @Override
    public void run()
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void death()
    {
        // TODO Auto-generated method stub

    }

}