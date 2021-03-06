package com.freiheit.fuava.simplebatch.result;

import com.freiheit.fuava.simplebatch.fetch.FetchedItem;

public class ResultStatistics {

    public static final class Builder<Input, Output> implements ProcessingResultListener<Input, Output> {

        private final Counts.Builder fetch = Counts.builder();
        private final Counts.Builder processing = Counts.builder();
        private boolean hasListenerDelegationFailures = false;

        public ResultStatistics build() {
            return new ResultStatistics(
                    fetch.build(),
                    processing.build(),
                    hasListenerDelegationFailures );
        }

        public void setListenerDelegationFailures( final boolean b ) {
            hasListenerDelegationFailures = b;
        }

        @Override
        public void onFetchResult( final Result<FetchedItem<Input>, Input> result ) {
            fetch.add( result );
        }

        @Override
        public void onProcessingResult( final Result<FetchedItem<Input>, Output> result ) {
            processing.add( result );
        }

    }

    private final Counts fetch;
    private final Counts processing;
    private final boolean hasListenerDelegationFailures;

    public ResultStatistics( final Counts fetch, final Counts persist, final boolean hasListenerDelegationFailures ) {
        this.fetch = fetch;
        this.processing = persist;
        this.hasListenerDelegationFailures = hasListenerDelegationFailures;
    }

    public Counts getFetchCounts() {
        return fetch;
    }

    public Counts getProcessingCounts() {
        return processing;
    }

    private static boolean allFailed( final Counts counts ) {
        return counts.getError() != 0 && counts.getSuccess() == 0;
    }

    private static boolean allSuccess( final Counts counts ) {
        return counts.getError() == 0;
    }

    public boolean isAllFailed() {
        return allFailed( fetch )
                || allFailed( processing );

    }

    public boolean isAllSuccess() {
        return allSuccess( fetch )
                && allSuccess( processing )
                && !hasListenerDelegationFailures();
    }

    public boolean hasListenerDelegationFailures() {
        return hasListenerDelegationFailures;
    }

    public static final <Input, Output> Builder<Input, Output> builder() {
        return new Builder<Input, Output>();
    }
}
