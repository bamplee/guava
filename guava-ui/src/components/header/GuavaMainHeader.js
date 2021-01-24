import React from 'react'

import {useHistory} from 'react-router-dom';

import classNames from 'classnames/bind';

import styles from './guavaHeader.module.scss';
import SearchOutlined from '@ant-design/icons/es/icons/SearchOutlined';
import {WingBlank} from 'antd-mobile';
import YuqueOutlined from '@ant-design/icons/es/icons/YuqueOutlined';
import HomeOutlined from '@ant-design/icons/es/icons/HomeOutlined';
import NumberOutlined from '@ant-design/icons/es/icons/NumberOutlined';

const cx = classNames.bind(styles);

const GuavaMainHeader = () => {
    const history = useHistory();

    return (
        <div className={cx('header_container')}>
            <div className={cx('title_container')}>
                <WingBlank>
                    {/*<div className={cx('left')} onClick={() => history.push('/intro')}>*/}
                    <div className={cx('left')}>
                        <NumberOutlined/>
                    </div>
                </WingBlank>
                <div className={cx('logo')} onClick={() => history.push('/search?type=map')}>
                    <span className={cx('search_title')}>지역 / 아파트 검색하기</span>
                    {/*<span className={cx('sub')}>SUBTITLE</span>*/}
                </div>
                <WingBlank>
                    <div className={cx('right')} onClick={() => history.push('/search?type=map')}>
                        <SearchOutlined/>
                    </div>
                </WingBlank>
            </div>
        </div>
    )
};

export default GuavaMainHeader;