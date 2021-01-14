import React from 'react'

import classNames from 'classnames/bind';

import styles from './guavaSpaceLine.scss';

const cx = classNames.bind(styles);

const GuavaSpaceLine = () => {

    return (
        <div className={cx('space_line')}/>
    )
};

export default GuavaSpaceLine;