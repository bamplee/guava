import React from 'react'

import {useHistory} from 'react-router-dom';

import classNames from 'classnames/bind';

import styles from './guavaHeader.module.scss';
import SearchOutlined from '@ant-design/icons/es/icons/SearchOutlined';
import {WingBlank} from 'antd-mobile';
import YuqueOutlined from '@ant-design/icons/es/icons/YuqueOutlined';

const cx = classNames.bind(styles);

const GuavaMainHeader = () => {
    const history = useHistory();

    return (
        <div className={cx('header_container')}>
            <div className={cx('title_container')}>
                <WingBlank>
                    <div className={cx('left')} onClick={() => history.push('/intro')}>
                        <YuqueOutlined/>
                    </div>
                </WingBlank>
                <div className={cx('logo')}>
                    <span className={cx('title')}>TITLE</span>
                    <span className={cx('sub')}>SUBTITLE</span>
                </div>
                <WingBlank>
                    <div className={cx('right')} onClick={() => history.push('/search')}>
                        <SearchOutlined/>
                    </div>
                </WingBlank>
            </div>
        </div>
    )
};

export default GuavaMainHeader;