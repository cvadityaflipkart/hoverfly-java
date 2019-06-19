.. _verification:

Verification
============


This feature lets you verify that specific requests have been made to the external service endpoints.

You can do request verification by calling the ``verify`` method from ``HoverflyRule``. It accepts two arguments.
The first one is a ``RequestMatcherBuilder`` which is also used by the DSL for creating simulations. It lets you define
your request pattern, and Hoverfly uses it to search its journal to find the matching requests. The second one is a
``VerificationCriteria`` which defines the verification criteria, such as the number of times a request was made.
If the criteria are omitted, Hoverfly Java expects the request to have been made exactly once.

Here are some examples:

.. code-block:: java

    // Verify exactly one request
    hoverfly.verify(
        service(matches("*.flight.*"))
            .get("/api/bookings")
            .anyQueryParams());

    // Verify exactly two requests
    hoverfly.verify(
        service("api.flight.com")
            .put("/api/bookings/1")
            .anyBody()
            .header("Authorization", "Bearer some-token"), times(2));


There are some useful ``VerificationCriteria`` static factory methods provided out-of-the-box. This will be familiar if you are a `Mockito <http://static.javadoc.io/org.mockito/mockito-core/2.8.47/org/mockito/Mockito.html#verify(T)>`_ user.

.. code-block:: java

    times(1)
    atLeastOnce(),
    atLeast(2),
    atMost(2),
    never()

``VerificationCriteria`` is a functional interface, meaning that you can provide your own criteria with a lambda expression. For example, you can create a more complex assertion on multiple request bodies, such as checking the transaction amount in a Charge object should keep increasing over time:

.. code-block:: java

    verify(service("api.payment.com").post("/v1/transactions").anyBody(),

        (request, data) -> {

            // Replace with your own criteria
            data.getJournal().getEntries().stream()
                    .sorted(comparing(JournalEntry::getTimeStarted))
                    .map(entry -> entry.getRequest().getBody())
                    .map(body -> {
                        try {
                            return new ObjectMapper().readValue(body, Charge.class);
                        } catch (IOException e) {
                            throw new RunTimeException();
                        }
                    })
                    .reduce((c1, c2) -> {
                        if(c1.getTransaction() > c2.getTransaction()) {
                            throw new HoverflyVerificationError();
                        }
                        return c2;
                    });
    });


If you want to verify all the stubbed requests were made at least once, you can use ``verifyAll``:

.. code-block:: java

    hoverfly.verifyAll();


You can also verify that an external service has never been called:

.. code-block:: java

    hoverfly.verifyZeroRequestTo(service(matches("api.flight.*")));


You can call ``verify`` as many times as you want, but requests are not verified in order by default. Support for verification in order will be added in a future release.


Resetting state
---------------

Verification is backed by a journal which logs all the requests made to Hoverfly. If multiple tests are sharing the same Hoverfly instance,
for example when you are using ``HoverflyRule`` with ``@ClassRule``, verification from one test might interfere with the requests triggered by another test.

In this case, you can reset the journal before each test to ensure a clean state for verifications:

.. code-block:: java

    @Before
    public void setUp() throws Exception {

        hoverfly.resetJournal();

    }
