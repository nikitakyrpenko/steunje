/**
 * $Id: Guid.java,v 1.0, 2006-05-09 13:45:19Z, Svetlana Bondar$
 */

package com.negeso.framework;

/**********************************************************************
 * Copyright (c) 2003 Hyades project.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;

/**
 * Abstract from IETF Specification
 * (http://ftp.ics.uci.edu/pub/ietf/webdav/uuid-guid/draft-leach-uuids-guids-01.txt):
 * <p/>
 * <i>This specification defines the format of UUIDs (Universally Unique
 * IDentifier), also known as GUIDs (Globally Unique IDentifier). A UUID is 128
 * bits long, and if generated according to the one of the mechanisms in this
 * document, is either guaranteed to be different from all other UUIDs/GUIDs
 * generated until 3400 A.D. or extremely likely to be different (depending on
 * the mechanism chosen). UUIDs were originally used in the Network Computing
 * System (NCS) [1] and later in the Open Software Foundation's (OSF)
 * Distributed Computing Environment [2].
 * <p/>
 * This specification is derived from the latter specification with the kind
 * permission of the OSF.
 * <p/>
 * References:
 * <ul>
 * <li>Lisa Zahn, et. al., Network Computing Architecture, Prentice Hall,
 * Englewood Cliffs, NJ, 1990</li>
 * <li>DCE: Remote Procedure Call, Open Group CAE Specification C309 ISBN
 * 1-85912-041-5 28cm. 674p. pbk. 1,655g. 8/94</li>
 * <li>R. Rivest, RFC 1321, "The MD5 Message-Digest Algorithm", 04/16/1992.
 * </li>
 * <li>NIST FIPS PUB 180-1, "Secure Hash Standard," National Institute of
 * Standards and Technology, U.S. Department of Commerce, DRAFT, May 31, 1994.
 * </li>
 * </ul>
 * <p/>
 * Copyright (c) 1990- 1993, 1996 Open Software Foundation, Inc. <br>
 * Copyright (c) 1989 by Hewlett-Packard Company, Palo Alto, Ca. & Digital
 * Equipment Corporation, Maynard, Mass. <br>
 * Copyright (c) 1998 Microsoft. <br>
 * <p/>
 * To anyone who acknowledges that this file is provided "AS IS" without any
 * express or implied warranty: permission to use, copy, modify, and distribute
 * this file for any purpose is hereby granted without fee, provided that the
 * above copyright notices and this notice appears in all source code copies,
 * and that none of the names of Open Software Foundation, Inc., Hewlett-Packard
 * Company, or Digital Equipment Corporation be used in advertising or publicity
 * pertaining to distribution of the software without specific, written prior
 * permission. Neither Open Software Foundation, Inc., Hewlett-Packard Company,
 * Microsoft, nor Digital Equipment Corporation makes any representations about
 * the suitability of this software for any purpose. </i>
 * <p/>
 * This class requires creating and deleting locking file (e.g. <default temp
 * file path>/guid.lock) to ensure the atomic generation of unique time stamps
 * between several JVM processes on the same system. Users with Java Security
 * enabled and without the required explicitly granted
 * <code>java.io.FilePermission</code> s (e.g. read, create and delete) are
 * not guaranteed to receive unique GUIDs between several JVM processes on the
 * same system.
 * <p/>
 * GUIDs are 128 bits in length. As such, the hexadecimal string representation
 * of the 128 bit <code>Guid</code> (e.g. see <code>toString()</code>) is
 * 32 characters long.
 * <p/>
 * The hexadecimal string representation of GUIDs generated with this class are
 * compliant with XML Schema <i>ID </i> primitive datatype
 * (http://www.w3.org/TR/2000/CR-xmlschema-2-20001024/#ID). As such, the most
 * significant hexadecimal character is guaranteed to be a non-numeric
 * character.
 * <p/>
 *
 * @version 1.0.1
 * @since 1.0
 */
public final class Guid
{

    /**
     * Hexadecimal characters.
     */
    private final static char[] HEXADECIMAL_CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Offset of time bewteen January 1, 1970 and October 15, 1582 in
     * 100-nanosecond resolution (0x01B21DD213814000L == 122192928000000000
     * 100-ns).
     */
    private final static long UTC_OFFSET = 0x01B21DD213814000L;

    private final static short GUID_VERSION_1 = 0x1000;

    private final static byte GUID_RESERVED = (byte) 0xC0;

    private final static int MAX_CLOCK_SEQ_ADJUST = 9999;

    private final static long MAX_WAIT_TIME = 1000;

    private final static Object lockObject = new Object();

    private static boolean _internalsSet = false;

    private static long _lastTimestamp = 0;

    private static int _clock_seq_adjust = 0;

    private static int _osProcessId = 0;

    private static int _rand_ia = 0;

    private static int _rand_ib = 0;

    private static int _rand_irand = 0;

    private static int _rand_m = 0;

    private static long clockSeq = 0;

    //***************************************************************************
    // Instance variables for the actual GUID
    //***************************************************************************

    /**
     * The low order 32 bits of the adjusted timestamp.
     */
    private int time_low = 0;

    /**
     * The middle order 16 bits of the adjusted timestamp.
     */
    private short time_mid = 0;

    /**
     * The high order 12 bits of the adjusted timestamp multiplexed with the
     * version number. The version number is in the most significant 4 bits.
     * <p/>
     * The following table lists currently defined versions of the UUID.
     * <p/>
     * <blockquote>
     * <p/>
     * <pre>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     *           Msb0  Msb1  Msb2  Msb3  Version  Description
     * <p/>
     *            0     0     0     1       1     The IEEE 802 address, time-based version.
     * <p/>
     *            0     0     1     0       2     Reserved for DCE Security version, with
     *                                            embedded POSIX UIDs.
     * <p/>
     *            0     0     1     1       3     The name-based version.
     * <p/>
     *            0     1     0     0       4     The pseudo-randomly generated version
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * </pre>
     * <p/>
     * </blockquote>
     * <p/>
     */
    private short time_hi_and_version = 0;

    /**
     * The high order 14 bits of the clock sequence multiplexed with two
     * reserved bits.
     * <p/>
     * For Guid version 1, the clock sequence is used to help avoid duplicates
     * that could arise when the clock is set backwards in time or if the node
     * ID changes.
     * <p/>
     * If the clock is set backwards, or even might have been set backwards
     * (e.g., while the system was powered off), and the Guid generator can not
     * be sure that no Guids were generated with timestamps larger than the
     * value to which the clock was set, then the clock sequence has to be
     * changed. If the previous value of the clock sequence is known, it can be
     * just incremented; otherwise it should be set to a random or high-quality
     * pseudo random value.
     * <p/>
     * Similarly, if the node ID changes (e.g. because a network card has been
     * moved between machines), setting the clock sequence to a random number
     * minimizes the probability of a duplicate due to slight differences in the
     * clock settings of the machines. (If the value of clock sequence
     * associated with the changed node ID were known, then the clock sequence
     * could just be incremented, but that is unlikely.)
     * <p/>
     * The clock sequence MUST be originally (i.e., once in the lifetime of a
     * system) initialized to a random number to minimize the correlation across
     * systems. This provides maximum protection against node identifiers that
     * may move or switch from system to system rapidly. The initial value MUST
     * NOT be correlated to the node identifier.
     * <p/>
     * For Guid version 3, it is a 14 bit value constructed from a name.
     * <p/>
     * For Guid version 4, it is a randomly or pseudo-randomly generated 14 bit
     * value.
     * <p/>
     */
    private byte clock_seq_hi_and_reserved = 0;

    /**
     * The low order 16 bits of the clock sequence.
     *
     * @see #clock_seq_hi_and_reserved
     */
    private byte clock_seq_low = 0;

    /**
     * For Guid version 1, the node field consists of the IEEE address, usually
     * the host address. For systems with multiple IEEE 802 addresses, any
     * available address can be used. The lowest addressed octet (octet number
     * 10) contains the global/local bit and the unicast/multicast bit, and is
     * the first octet of the address transmitted on an 802.3 LAN.
     * <p/>
     * For systems with no IEEE address, a randomly or pseudo-randomly generated
     * value may be used (see section 4). The multicast bit must be set in such
     * addresses, in order that they will never conflict with addresses obtained
     * from network cards.
     * <p/>
     * For Guid version 3, the node field is a 48 bit value constructed from a
     * name.
     * <p/>
     * For Guid version 4, the node field is a randomly or pseudo-randomly
     * generated 48 bit value.
     * <p/>
     */
    private static byte[] _ieee802Addr = new byte[6];

    /**
     * No-argument constructor
     * <p/>
     * Creates a variant #1 style identifier and returns the instance.
     * <p/>
     * The adjusted time stamp is split into three fields, and the clockSeq is
     * split into two fields.
     * <p/>
     * <p/>
     * <pre>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     *         |&lt;------------------------- 32 bits --------------------------&gt;|
     * <p/>
     *         +--------------------------------------------------------------+
     *         |                     low 32 bits of time                      |  0-3  .time_low
     *         +-------------------------------+-------------------------------
     *         |     mid 16 bits of time       |                                 4-5  .time_mid
     *         +-------+-----------------------+
     *         | vers. |   hi 12 bits of time  |                                 6-7  .time_hi_and_version
     *         +-------+-------+---------------+
     *         |Res|  clkSeqHi |                                                   8   .clock_seq_hi_and_reserved
     *         +---------------+
     *         |   clkSeqLow   |                                                   9   .clock_seq_low
     *         +---------------+----------...-----+
     *         |            node ID               |                             8-16   .node
     *         +--------------------------...-----+
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * </pre>
     * <p/>
     * <p/>
     */
    public Guid()
    {

        long adjustedTimestamp = 0;

        // Instead of having small synchronized pieces as it was done before,
        // it is necessary to have one long uninterrupted .
        // Thus we prevent multiple access to _clock_seq_adjust and
        // _lastTimestamp values.
        synchronized (lockObject)
        {

            // The first time this method is called, it will need to obtain the
            // operating system internals. It uses the JNI functions defined
            // earlier, and will set a static variable to avoid this in
            // subsequent
            //calls.
            if (_internalsSet == false)
            {

                getPseudoIEEE802Address(_ieee802Addr);

                _osProcessId = getPseudoOSProcessId();

                initTrueRandom(getAdjustedTimestamp());

                clockSeq = getTrueRandom();

                _internalsSet = true;
            }

            // Get the adjusted timestamp and use to generate a random clock
            // sequence.
            // We must handle the situation where the current time is the same
            // as the previous generation or has actually gone backwards.
            boolean timeIsValid = true;

            do
            {
                adjustedTimestamp = getAdjustedTimestamp();

                // Clock has been reset, generate a new random clockSeq. The two
                // most significant bits will be set to the reserved value
                // later.
                if (adjustedTimestamp < _lastTimestamp)
                {
                    clockSeq = getTrueRandom();
                    _clock_seq_adjust = 0;
                }

                // Normal situation, reset the adjustment and leave the do-while
                // loop.
                if (adjustedTimestamp > _lastTimestamp)
                {
                    _clock_seq_adjust = 0;
                }

                // Clock hasn't changed resolution, adjust the clock sequence
                // for uniqueness.
                // If we hit the maximum adjustment, we must "spin" until the
                // clock is incremented.
                if (adjustedTimestamp == _lastTimestamp)
                {

                    if (_clock_seq_adjust < MAX_CLOCK_SEQ_ADJUST)
                    {
                        _clock_seq_adjust++;
                    }
                    else
                    {
                        timeIsValid = false;
                    }
                }
            }
            while (!timeIsValid);

            _lastTimestamp = adjustedTimestamp;

            if (_clock_seq_adjust != 0)
            {
                adjustedTimestamp += _clock_seq_adjust;
            }

            // Construct a Version 1 GUID from the component pieces and
            // constants. The variable tempValue allows us to perform masking
            // and bit shifting operations separate from casting the value into
            // the appropriate size.
            long tempValue = adjustedTimestamp & 0xFFFFFFFF;
            time_low = (int) tempValue;

            tempValue = (adjustedTimestamp >>> 32) & 0xFFFF;
            time_mid = (short) tempValue;

            tempValue = (adjustedTimestamp >>> 48) & 0x0FFF;
            time_hi_and_version = (short) tempValue;
            time_hi_and_version |= GUID_VERSION_1;

            tempValue = clockSeq & 0xFF;
            clock_seq_low = (byte) tempValue;

            tempValue = (clockSeq & 0x3F00) >>> 8;
            clock_seq_hi_and_reserved = (byte) tempValue;
            clock_seq_hi_and_reserved |= GUID_RESERVED;
        }
    }

    /**
     * Initializes true number values generator.
     */
    private synchronized void initTrueRandom(long adjustedTimestamp)
    {

        // This random number generator (RNG) is based on Note 16.7, July 7,
        // 1989
        // by Robert Gries, Cambridge Research Lab, Computational Quality Group
        //
        // It is really a "Multiple Prime Random Number Generator" (MPRNG) and
        // is
        // completely discussed in Reference 1.
        //
        //   References:
        //   1) "The Multiple Prime Random Number Generator" by Alexander Hass
        //      pp. 368 to 381 in ACM Transactions on Mathematical Software,
        //      December, 1987
        //   2) "The Art of Computer Programming: Seminumerical Algorithms
        //      (vol 2)" by Donald E. Knuth, pp. 39 to 113.
        //
        // A summary of the notesfile entry follows:
        //
        // Gries discusses the two RNG's available for ULTRIX-C. The default RNG
        // uses a Linear Congruential Method (very popular) and the second RNG
        // uses
        // a technique known as a linear feedback shift register.
        //
        // The first (default) RNG suffers from bit-cycles
        // (patterns/repetition),
        // ie. it's "not that random."
        //
        // While the second RNG passes all the emperical tests, there are
        // "states"
        // that become "stable", albeit contrived.
        //
        // Gries then presents the MPRNG and says that it passes all emperical
        // tests listed in Reference 2. In addition, the number of calls to the
        // MPRNG before a sequence of bit position repeats appears to have a
        // normal
        // distribution.
        //
        // The code uses the Gries's MPRNG with the same constants that were
        // presented in his paper. These may not be "ideal" for the range of
        // numbers we are dealing with.
        _rand_m = 971;
        _rand_ia = 11113;
        _rand_ib = 104322;
        _rand_irand = 4181;

        // Generating our 'seed' value
        //
        // We start with the current time, but, since the resolution of clocks
        // is
        // system hardware dependent and most likely coarser than our resolution
        // (100 nanosecond increments), we 'mixup' the bits by xor'ing all the
        // bits together in 16-bit chunks. This will have the effect of
        // involving all of the bits in the determination of the seed value
        // while
        // remaining system independent. To ensure a unique seed when there
        // are multiple processes creating Guid's on a system, we add in the
        // PID.
        int seed = ((int) (adjustedTimestamp >>> 48)) ^ ((int) (adjustedTimestamp >>> 32)) ^ ((int) (adjustedTimestamp >>> 16)) ^ ((int) (adjustedTimestamp & 0x000000000000FFFF));

        _rand_irand = _rand_irand + seed + _osProcessId;
    }

    /**
     * Get true random value.
     */
    private synchronized short getTrueRandom()
    {

        // Increment the Multiplier and Adders
        _rand_m = _rand_m + 7;
        _rand_ia = _rand_ia + 1907;
        _rand_ib = _rand_ib + 73939;

        // Keep the Multiplier and Adders within the specified range
        if (_rand_m >= 9973)
        {
            _rand_m = _rand_m - 9871;
        }

        if (_rand_ia >= 99991)
        {
            _rand_ia = _rand_ia - 89989;
        }

        if (_rand_ib >= 224729)
        {
            _rand_ib = _rand_ib - 96233;
        }

        // Calculate the new Random Value (from previous or seed)
        _rand_irand = (_rand_irand * _rand_m) + _rand_ia + _rand_ib;
        _rand_irand = (_rand_irand >>> 16) ^ (_rand_irand & 0x00003FFF);

        return ((short) _rand_irand);
    }

    /**
     * This call originally would use JNI to return the process ID of the java
     * program. Because we do not want to support JNI on all the platforms we
     * will simply get the time stamp. Because the value that is being returned
     * should be an int and not a long we modulus by 1000000000 and return the
     * lowest significant 9 numbers.
     *
     * @return the current time in seconds
     */
    private synchronized int getPseudoOSProcessId()
    {
        return ((int) (getUniqueTimeStamp() % 1000000000));
    }

    /**
     * This call originally would use JNI to return the IEEE 802 addrsss from
     * the system. Because we do not want to support JNI on all the platforms we
     * be using an alternative method of generating a value for an IEEE 802 that
     * is describe in the internet draft.
     * <p/>
     * The following is the excert from the draft:
     * <p/>
     * 4. Node IDs when no IEEE 802 network card is available
     * <p/>
     * If a system wants to generate UUIDs but has no IEE 802 compliant network
     * card or other source of IEEE 802 addresses, then this section describes
     * how to generate one.
     * <p/>
     * The ideal solution is to obtain a 47 bit cryptographic quality random
     * number, and use it as the low 47 bits of the node ID, with the most
     * significant bit of the first octet of the node ID set to 1. This bit is
     * the unicast/multicast bit, which will never be set in IEEE 802 addresses
     * obtained from network cards; hence, there can never be a conflict between
     * UUIDs generated by machines with and without network cards.
     * <p/>
     * If a system does not have a primitive to generate cryptographic quality
     * random numbers, then in most systems there are usually a fairly large
     * number of sources of randomness available from which one can be
     * generated. Such sources are system specific, but often include:
     * <p>- the percent of memory in use in bytes - the size of main memory in
     * bytes - the amount of free main memory in bytes - the size of the paging
     * or swap file in bytes - free bytes of paging or swap file - the total
     * size of user virtual address space in bytes - the total available user
     * address space bytes - the size of boot disk drive in bytes - the free
     * disk space on boot drive in bytes - the current time - the amount of time
     * since the system booted - the individual sizes of files in various system
     * directories - the creation, last read, and modification times of files in
     * various system directories - the utilization factors of various system
     * resources (heap, etc.) - current mouse cursor position - current caret
     * position - current number of running processes, threads - handles or IDs
     * of the desktop window and the active window - the value of stack pointer
     * of the caller - the process and thread ID of caller - various processor
     * architecture specific performance counters (instructions executed, cache
     * misses, TLB misses)
     * <p/>
     * (Note that it precisely the above kinds of sources of randomness that are
     * used to seed cryptographic quality random number generators on systems
     * without special hardware for their construction.)
     * <p/>
     * In addition, items such as the computer's name and the name of the
     * operating system, while not strictly speaking random, will help
     * differentiate the results from those obtained by other systems.
     * <p/>
     * The exact algorithm to generate a node ID using these data is system
     * specific, because both the data available and the functions to obtain
     * them are often very system specific. However, assuming that one can
     * concatenate all the values from the randomness sources into a buffer, and
     * that a cryptographic hash function such as MD5 [3] is available, then any
     * 6 bytes of the MD5 hash of the buffer, with the multicast bit (the high
     * bit of the first byte) set will be an appropriately random node ID.
     * <p/>
     */
    private synchronized void getPseudoIEEE802Address(byte[] ieee802Addr)
    {

        byte[] currentTime = String.valueOf(getUniqueTimeStamp()).getBytes();
        byte[] localHostAddress = getLocalHostAddress();
        byte[] inMemObj = new Object().toString().getBytes();
        byte[] freeMemory = String.valueOf(Runtime.getRuntime().freeMemory()).getBytes();
        byte[] totalMemory = String.valueOf(Runtime.getRuntime().totalMemory()).getBytes();

        byte[] hashcode = null;
        byte[] bytes = new byte[freeMemory.length + totalMemory.length + currentTime.length + localHostAddress.length + inMemObj.length];

        int bytesPos = 0;

        System.arraycopy(currentTime, 0, bytes, bytesPos, currentTime.length);
        bytesPos += currentTime.length;

        System.arraycopy(localHostAddress, 0, bytes, bytesPos, localHostAddress.length);
        bytesPos += localHostAddress.length;

        System.arraycopy(inMemObj, 0, bytes, bytesPos, inMemObj.length);
        bytesPos += inMemObj.length;

        System.arraycopy(freeMemory, 0, bytes, bytesPos, freeMemory.length);
        bytesPos += freeMemory.length;

        System.arraycopy(totalMemory, 0, bytes, bytesPos, totalMemory.length);
        bytesPos += totalMemory.length;

        try
        {

            MessageDigest md5 = MessageDigest.getInstance("MD5");

            md5.reset();

            hashcode = md5.digest(bytes); /* Compute hashcode */
        }
        catch (Exception e)
        {
        }

        System.arraycopy(hashcode, 0, ieee802Addr, 0, 6);

        //Turn on the highorder bit of the first byte:
        ieee802Addr[0] |= 0x80;
    }

    private synchronized byte[] getLocalHostAddress()
    {

        try
        {
            return (InetAddress.getLocalHost().getAddress());
        }
        catch (UnknownHostException u)
        {
            return (new byte[]{127, 0, 0, 1});
        }
    }

    /**
     * Obtains the current timestamp and adjust its value
     */
    private synchronized long getAdjustedTimestamp()
    {

        // The system time is recorded as the number of milliseconds since
        // January 1, 1970. The coordinated universal time is measured from
        // the begininning of the Gregorian Calendar, so we must adjust back
        // with the number of milliseconds between October 15, 1582 and
        // January 1, 1970. Then we convert into 100-nanosecond resolution by
        // multiplying by 10**4.
        return ((System.currentTimeMillis() * 10000L) + UTC_OFFSET);
    }

    private synchronized long getUniqueTimeStamp()
    {

        //Lock file for ensuring we create a unique time stamp for all
        // processes on the system:
        File lockFile = new File(System.getProperty("java.io.tmpdir"), "guid.lock");

        //Create a default time stamp:
        long timeStamp = System.currentTimeMillis();

        try
        {

            //Last modified time stamp for the lock file:
            long lastModified = lockFile.lastModified();

            //Maximum absolute time stamp to execute the lock file algorithm:
            //NOTE: This upperbound provides an escape mechanism for the
            // scenario
            // where a previous instance
            //of the JVM crashed and a stray lock file persists on the local
            // file
            // system.
            long maxWaitTimeStamp = (System.currentTimeMillis() + MAX_WAIT_TIME);

            //Loop until we can create the lock file (e.g. exclusive rights)
            // since
            // creating a file is atomic or the
            //maximum absolute time stamp to execute the lock file algorithm
            // has
            // expired:
            while (true)
            {

                try
                {

                    //After the lock file has been created, we have exclusive
                    // rights to the lock file and enter the process
                    // synchronized
                    // block:
                    if (lockFile.createNewFile())
                    {
                        break;
                    }

                    //If we cannot create the lock file, only continue until
                    // the
                    // maximum absolute time stamp to execute the lock file
                    // algorithm has expired:
                    else if (System.currentTimeMillis() > maxWaitTimeStamp)
                    {

                        //If the lock file has not been updated before the
                        // maximum
                        // absolute time stamp to execute the lock file
                        // algorithm
                        // has expired, delete the lock file and attempt to
                        // execute
                        // the lock algorithm again:
                        if (lockFile.lastModified() <= lastModified)
                        {

                            lockFile.delete();

                            //Only permit this instance of the algorithm to
                            // attempt
                            // to delete the lock file once:
                            lastModified = -1;
                        }

                        //If a lock cannot be obtained before the maximum
                        // absolute
                        // time stamp to execute the lock file algorithm has
                        // expired, return the non-atomic current time stamp:
                        else
                        {
                            return (System.currentTimeMillis());
                        }
                    }
                }
                catch (IOException i)
                {
                    //Ignore since the lock file exists.
                }
            }

            //Wait 1/10 second (100 milliseconds) to ensure a unique time
            // stamp:
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException i)
            {
                //Ignore since we are only sleeping.
            }

            //Create a unique time stamp as an atomic operation:
            timeStamp = System.currentTimeMillis();
        }
        catch (SecurityException s)
        {
            //Ignore all security exceptions.
        }
        finally
        {

            try
            {

                //After the lock file has been atomically deleted, we release
                // the
                // exclusive rights to the lock file exit the process
                // synchronized
                // block:
                if (lockFile != null)
                {
                    lockFile.delete();
                }
            }
            catch (SecurityException s)
            {
                //Ignore all security exceptions.
            }
        }

        return timeStamp;
    }

    /**
     * Returns the hexadecimal string representation of the 128 bit
     * <code>Guid</code> object.
     * <p/>
     * The <code>toString</code> method will take the binary data in each of
     * the internal components of the <code>Guid</code> object, covert each
     * byte to its hexadecimal character equivalent, and return the string.
     * <p/>
     *
     * @return The hexadecimal string representation of the 128 bit
     *         <code>Guid</code>, which is 32 characters long.
     */
    public String toString()
    {

        final char[] stringBuffer = new char[32];

        //Current position in the local string buffer:
        int pos = 0;

        stringBuffer[pos++] = HEXADECIMAL_CHARACTERS[(clock_seq_hi_and_reserved >>> 4 & 0xF)];
        stringBuffer[pos++] = HEXADECIMAL_CHARACTERS[(clock_seq_hi_and_reserved & 0xF)];
        stringBuffer[pos++] = HEXADECIMAL_CHARACTERS[(clock_seq_low >>> 4 & 0xF)];
        stringBuffer[pos++] = HEXADECIMAL_CHARACTERS[(clock_seq_low & 0xF)];

        int i = 0;

        while (pos < 16)
        {

            stringBuffer[pos] = HEXADECIMAL_CHARACTERS[(_ieee802Addr[i] >>> 4 & 0xF)];

            stringBuffer[pos + 1] = HEXADECIMAL_CHARACTERS[(_ieee802Addr[i] & 0xF)];

            i++;

            pos += 2;
        }

        //Number of bits to shift the value being converted to hexadecimal:
        int shift = 28;

        while (pos < 24)
        {

            stringBuffer[pos] = HEXADECIMAL_CHARACTERS[(time_low >>> shift & 0xF)];

            shift -= 4;

            pos++;
        }

        shift = 12;

        while (pos < 28)
        {

            stringBuffer[pos] = HEXADECIMAL_CHARACTERS[(time_mid >>> shift & 0xF)];

            shift -= 4;

            pos++;
        }

        shift = 12;

        while (pos < 32)
        {

            stringBuffer[pos] = HEXADECIMAL_CHARACTERS[(time_hi_and_version >>> shift & 0xF)];

            shift -= 4;

            pos++;
        }

        return (new String(stringBuffer).trim());
    }

    /**
     * Static convenience API for generating a new GUID.
     * <p/>
     * This API is equivalent to calling <code>new Guid().toString()</code>.
     * <p/>
     * The API returns the hexadecimal string representation of a new
     * <code>Guid</code> object.
     * <p/>
     * This API will create a new <code>Guid</code> object, take the binary
     * data in each of the internal components of the <code>Guid</code>
     * object, covert each byte to its hexadecimal character equivalent and
     * return the string.
     * <p/>
     *
     * @return The hexadecimal string representation of a new <code>Guid</code>
     *         object, which is 32 characters long.
     */
    public static String generate()
    {
        return (new Guid().toString());
    }

    /**
     * Test main method.
     * 
     * This method generates a set number of GUIDs and checks for any duplicate
     * generated GUID values. public static void main(String[] args) {
     * 
     * final int TOTAL_GUIDS = 100000;
     * 
     * String[] guids = new String[TOTAL_GUIDS];
     * 
     * System.out.println("Generating GUIDs...");
     * 
     * long startTime = System.currentTimeMillis();
     * 
     * for (int counter = 0; counter < TOTAL_GUIDS; counter++) { guids[counter] =
     * Guid.generate(); }
     * 
     * long endTime = System.currentTimeMillis();
     * 
     * System.out.println("Comparing generated GUIDs...");
     * 
     * Hashtable generatedGuids = new Hashtable();
     * 
     * for (int counter = 0; counter < TOTAL_GUIDS; counter++) {
     * 
     * if (generatedGuids.containsKey(guids[counter])) {
     * generatedGuids.put(guids[counter], new Integer(((Integer)
     * (generatedGuids.get(guids[counter]))).intValue() + 1)); }
     * 
     * else { generatedGuids.put(guids[counter], new Integer(1)); } }
     * 
     * Enumeration enumeration = generatedGuids.keys(); String guid = null; int
     * totalDuplicateGuids = 0;
     * 
     * System.out.println("Checking for duplicate GUIDs...");
     * 
     * while (enumeration.hasMoreElements()) {
     * 
     * guid = ((String) (enumeration.nextElement()));
     * 
     * if (((Integer) (generatedGuids.get(guid))).intValue() > 1) {
     * 
     * System.out.println("'" + guid + "' has been duplicated " +
     * generatedGuids.get(guid) + " times.");
     * 
     * totalDuplicateGuids++; } }
     * 
     * if (totalDuplicateGuids != 0) { System.out.println("No duplicate GUIDs
     * found!"); }
     * 
     * System.out.println("Summarizing performance...");
     * 
     * System.out.println("Total Time: " + (endTime - startTime) + " ms/" +
     * (((double) ((((double) (endTime - startTime)) / ((double) (1000)))))) + "
     * s"); System.out.println("Total GUIDS: " + TOTAL_GUIDS);
     * System.out.println("Total duplicate GUIDS: " + totalDuplicateGuids);
     * System.out.println("Time/GUID: " + ((double) ((((double) (endTime -
     * startTime)) / ((double) (TOTAL_GUIDS))))));
     * 
     * System.out.println("Done!"); }
     */
}