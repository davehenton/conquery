import React                        from 'react';
import PropTypes                    from 'prop-types';
import { connect }                  from 'react-redux';
import T                            from 'i18n-react';
import { IconButton }               from '../button';
import { clearTimebasedQuery }      from './actions';
import { anyConditionFilled }       from './helpers';

const TimebasedQueryClearButton = (props) => {
  return (
    <div className="query-clear-button">
      <IconButton
        onClick={props.clearQuery}
        className="btn btn--small btn--transparent"
        label={T.translate("common.clear")}
        iconClassName="fa-trash-o"
        disabled={!props.isEnabled}
      />
    </div>
  );
};

TimebasedQueryClearButton.propTypes = {
  clearQuery: PropTypes.func.isRequired,
  isEnabled: PropTypes.bool.isRequired,
};

const mapStateToProps = (state) => ({
  isEnabled: state.panes.right.tabs.timebasedQueryEditor.timebasedQuery.conditions.length > 1 ||
    anyConditionFilled(state.panes.right.tabs.timebasedQueryEditor.timebasedQuery),
});

const mapDispatchToProps = (dispatch) => ({
  clearQuery: () => dispatch(clearTimebasedQuery()),
})

export default connect(mapStateToProps, mapDispatchToProps)(TimebasedQueryClearButton);
