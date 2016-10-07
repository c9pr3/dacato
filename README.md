# DACATO Data access- and transfer object library
(the "T" is quite silent...)

A project written by [Christian Senkowski](http://e-cs.co/).


## About

DACATO aims to be a asynchronous, cachable data access framework/library for small to middle projects which have to interact with a **relational** database.
While most frameworks are bloated and force the user to do things in a*specific*way, DACATO offers full flexibility. You *may* do things as provided or overwrite everything to have it *your* way.

It has been tested with MySQL, HSQL and PostgreSQL. If you use it successfully with a different database, please let me know!


## Installation

<h5>pom.xml</h5>
```
    <repositories>
        <repository>
            <id>dacato-mvn-repo</id>
            <name>git</name>
            <url>https://raw.githubusercontent.com/Adar/dacato/master/mvn-repo/</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>co.ecs</groupId>
            <artifactId>dacato</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
```


## Basic Usage

As in most ORMish frameworks/libs for each table you need two classes. 
Let's assume you have a table "customer" and fields "id", "first_name", "last_name" and "customer_number".

You first create a class "Customer" which represents a single row. This class implements "DatabaseEntity" with type of the primary key, in this case "Long".

```
public final class Customer implements DatabaseEntity<Long> {
    private static final String TABLE_NAME = "customer";
    private static final String QUERY = String.format("SELECT %%s FROM %s WHERE %%s = ?", TABLE_NAME);
    private final Long id;
    private final ApplicationConfig config;
    private final AtomicBoolean objectValid = new AtomicBoolean(true);

    public Customer(final ApplicationConfig config, final long id) {
        this.id = id;
        this.config = config;
    }

    @Override
    public Long primaryKey() {
        return this.id;
    }

    public CompletableFuture<DatabaseResultField<String>> firstName() {
        return this.findOne(new SingleColumnQuery<>(QUERY, Fields.FIRST_NAME, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    public CompletableFuture<DatabaseResultField<String>> lastName() {
        return this.findOne(new SingleColumnQuery<>(QUERY, Fields.LAST_NAME, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    public CompletableFuture<DatabaseResultField<Long>> number() {
        return this.findOne(new SingleColumnQuery<>(QUERY, Fields.NUMBER, Fields.ID, this.primaryKey()), () ->
                this.objectValid);
    }

    @Override
    public CompletableFuture<DatabaseEntity<Long>> save(ColumnList columnValuesToSet) {
        final SingleColumnUpdateQuery<Long> query = new SingleColumnUpdateQuery<>(
                "UPDATE customer SET %s WHERE %%s = ?", Fields.ID, id, columnValuesToSet);
        final CompletableFuture<Integer> updated = this.update(query, () -> this.objectValid);
        this.objectValid.set(false);
        return updated.thenApply(l -> new Customer(config, id));
    }

    @Override
    public ApplicationConfig config() {
        return this.config;
    }

    static final class Fields {
        static final DatabaseField<Long> ID = new DatabaseField<>("id", Long.class, Types.BIGINT);
        static final DatabaseField<Long> NUMBER = new DatabaseField<>("customer_number", Long.class, Types.BIGINT);
        static final DatabaseField<String> FIRST_NAME = new DatabaseField<>("customer_first_name", String.class, Types.VARCHAR);
        static final DatabaseField<String> LAST_NAME = new DatabaseField<>("customer_last_name", String.class, Types.VARCHAR);

        private Fields() {
            //unused
        }
    }
}

```

After that you create a class "Customers" which represents a table and acts as container.
Implement "DatabaseTable" with type of the primary key and corresponding entity class.
 
 ```
 public final class Customers implements DatabaseTable<Long, Customer> {
 
     private final ApplicationConfig config;
 
     public Customers(final ApplicationConfig config) {
         this.config = config;
     }
 
     CompletableFuture<Boolean> removeAll() {
         return truncate("TRUNCATE TABLE customer");
     }
 
     @Override
     public CompletableFuture<Customer> findOne(final Long id) {
         return this.findOne(new SingleColumnQuery<>("SELECT %s FROM customer WHERE %s = ?", Customer.Fields.ID,
                 Customer.Fields.ID, id)).thenApply(foundId -> new Customer(config, foundId.resultValue()));
     }
 
     public CompletableFuture<Customer> add(final String customerFirstName, final String customerLastName,
                                            final long customerNumber) {
         final InsertQuery<Long> query = new InsertQuery<>("INSERT INTO customer (%s, %s, %s, %s) " +
                 "VALUES (null, ?, ?, ?)", Customer.Fields.ID);
         query.add(Customer.Fields.FIRST_NAME, customerFirstName);
         query.add(Customer.Fields.LAST_NAME, customerLastName);
         query.add(Customer.Fields.NUMBER, customerNumber);
         return add(query).thenApply(id -> new Customer(config, id.resultValue()));
     }
 
     @Override
     public CompletableFuture<List<Customer>> findAll() {
         return this.findMany(new SingleColumnQuery<>("SELECT %s FROM customer", Customer.Fields.ID))
                 .thenApply(list -> list.stream().map(foundId -> new Customer(config, foundId.resultValue()))
                         .collect(Collectors.toList()));
     }
 
     @Override
     public ApplicationConfig config() {
         return config;
     }
 }
 ```

That's it - You may now query the customer table in any way you like.<br/>
**If you want to have it cached, implement CachedDatabaseTable and CachedDatabaseEntity.**<br/>

Well, a class which implements ApplicationConfig is needed, too. You may use your application config and additionally implement ApplicationConfig.

For a more completed example, see [Dacato-Ref](http://github.com/Adar/dacato-ref "Dacato Reference Implementation") 


## Why the ... should I use DACATO? Tons of more major frameworks out there!

You may say "What? Same sh\*t as anything else while Hibernate and others offer me more." - and you are right.
Dacato is a *small* framework. It offers not much while still enough to solve *one* problem.

Pro's:
* **Small**
* **Cachable**
* **Immutable** design
* **Asynchronous** with CompletableFuture which offers great flexibility.
* **No dependencies**
* **No annotations** (hence no reflection)
* **Flexible** to a point where you can totally ignore what I had in mind and do your own thing. 


## Why is the "T" silent?

DACATO does not fully transfer. It does not provide an abstraction layer for SQL - 
but a layer upon queries themselves for typesafety reasons.


## License

This software takes the MIT license as described below:

Copyright (C) 2016 by Christian Senkowski

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.