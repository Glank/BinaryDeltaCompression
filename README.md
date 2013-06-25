BinaryDeltaCompression
======================

What is delta compression and why do I care?
--------------------------------------------
In layman's terms, delta compression (or delta encoding) is the process of storing the difference between two files or pices of data.
This is very useful when you have large files that you constantly make changes to and backups of;
you can save a lot of space by recording the changes to the files rather than complete copies.

This project is my own implementation of a tiny binary delta compression algorithm.
This distribution comes with an example GUI application that can be used to create and revert from compressed backup files.
To run the application, download the "BackupTool_1_1.jar" and double click on it: if you have java installed correctly it should open a new window.
If you have trouble running it that way, you can go into the command prompt or terminal and type "java -jar BackupTool_1_1.jar".

For developers, there are two simple usages of this code...

In Memory
---------

First is an in-memory backup of some raw binary data:

    //you have some old data and some current data
    byte[] old, current;
    ...
    //you can store the old data using the current dat
    Backup backup = new Backup(old,current); a
    ...
    //then retrieve it from the current data at a later date
    old = backup.revert(current);
    
The kirstein.compress.Backup object is serializable, so it can be easily converted to and from binary as such:

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(backup);
    byte[] backupBytes = baos.toByteArray();
    ...
    ByteArrayInputStream bais = new ByteArrayInputStream(backupBytes);
    ObjectInputStream ois = new ObjectInputStream(bais);
    Backup backup = (Backup)ois.readObject();

Optionally, the included ZipUtil can be used to further deflate the data at the expense of a little time:

    bytes = ZipUtil.compress(bytes);
    ...
    bytes = ZipUtil.decompress(bytes);

On Disk
-------

You can also store a stack of backups to a kirstein.compress.BackupFile.
    
    BackupFile backups = new BackupFile(new java.io.File("where/to/put/backup.data"))
    ...
    //you can then store stacks of binary data or file instances
    backups.backup(some_data); //like a stack.push operation
    ...
    //and retrieve it like so
    some_data = backups.getReversion(); //like a stack.pop operation
