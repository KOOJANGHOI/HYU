# **Project_4 : File System**


## <1> Goal
--------
####  Expand the maximum size a file can have. 

 - Each file in xv6 is associated with an inode , which is metadata.

 - Default inode provide Direct pointer , Single indirect.

 - Additionally, i will increase maximum capacity using Double indirect.

---

## <2> Default option
--------

 - Change 'FSSIZE'(in param.h) 1000 to 2000. this allows to store more than 1Kb files.

 - To contain the address for double indirect in the inode , decrease 'NDIRECT' 12 to 11. also, change 'MAXFILE' to  (NDIRECT + NINDIRECT + NINDIRECT*NINDIRECT).(in fs.h)

 - Change the size of the 'addr' array of the inode,dnode structure to 'NDIRECT+2'.(in file.h and fs.h)

----

## <3> Implement(in fs.c)
--------

 - In bmap(struct inode *ip , uint bn) function.

 - The bmap function finds an empty block, allocates one, and returns its address value.

  - bmap func perform other processing depending on the 'bn' which is passed as an argument. If( 0 < bn < NDIRECT - Direct pointer). If( NDIRECT < bn < NINDIRECT - Single indirect). If( NINDIRECT < bn < NINDIRECT*NINDIRECT - Double indirect).

  - If ip->addrs[NDIRECT+1] is empty , Allocate free block.(for Double indirect block).
call bread from the block address and store return value in buffer 'bp'. and insert bp->data to (uint)'a'.

  - If first-address array is empty(index is (bn/NINDIRECT)) , Allocate free block. and insert the block address into first-array. and write log. call 'brelse' func about 'bp'. and call 'bread' func and store data to a.(same as upper line).

  - like wise first-array , same task is done in the second array. but index of array is (bn%NINDIRECT). finally , call 'brelse' func about 'bp' and return block number.

 - In itrunc(struct inode *ip)

 - The itrunc func deallocate all blocks in inode and reset the size to 0. 

  - In Direct block , at specific index ,  just call 'bfree' func and reset the size to 0

  - In Single indirect block , call the bread function to create an array with the address of the data. If there is data in the array, call the bfree function and make the size zero. Finally, the buffer is freed and the bfree function is called to deallocate a particular block of the ip-> addr array and make its size to zero.

  - In Double indirect block ,  same as single indirect method. Just be careful to deallocate from the end.

 ---

## <4>Problems and Divised solutions

----

 - In bmap fucntion , The problem was how to envision an algorithm to manage index of array. As a result, I have devised a way to manage the index through quotient and remainder divided by 'NINDIRECT' similar to the page management technique.

 - In itrunc function , The Problem was how to deallocate a large number of blocks in double indirect. As a result, I used the method of deallocating blocks from the end like single indirect method.

----

 
