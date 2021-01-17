import React, {useEffect, useRef, useState} from 'react'

import {useHistory, useLocation} from 'react-router-dom';

import classNames from 'classnames/bind';

import styles from './guavaHeader.module.scss';
import {useRecoilState} from 'recoil';
import {levelState, regionState} from '../datatool/state';
import ArrowLeftOutlined from '@ant-design/icons/es/icons/ArrowLeftOutlined';
import {List, WingBlank} from 'antd-mobile';
import {fetchSearch} from '../datatool/api';
import CloseOutlined from '@ant-design/icons/es/icons/CloseOutlined';
import ReconciliationOutlined from '@ant-design/icons/es/icons/ReconciliationOutlined';
import EnvironmentOutlined from '@ant-design/icons/es/icons/EnvironmentOutlined';
import Highlighter from 'react-highlight-words';

const cx = classNames.bind(styles);

const GuavaSearchHeader = () => {
    const location = useLocation();
    const history = useHistory();
    const queryInput = useRef(null);
    const [query, setQuery] = useState('');
    const [queryList, setQueryList] = useState([]);
    const [loading, setLoading] = useState(false);
    const [level, setLevel] = useRecoilState(levelState);
    const [region, setRegion] = useRecoilState(regionState);

    useEffect(() => {
        if (queryInput && queryInput.current) {
            queryInput.current.focus();
        }
    }, [queryInput]);

    useEffect(() => {
        fetchRegion();
    }, [query]);

    const onChangeQuery = (e) => {
        let query = e.target.value;
        setQuery(query);
        if (query.length === 0) {
            setQueryList([]);
        }
    };

    const fetchRegion = async () => {
        if (query !== '') {
            const pattern = /([^가-힣a-z\x20])/i;
            if (!pattern.test(query)) {
                setLoading(true);
                let regionList = await fetchSearch(query);
                setQueryList(regionList);
                setLoading(false);
            }
        }
    };

    const handleResultItem = async (item) => {
        setRegion(item);
        if (item.type === 'BUILDING') {
            setLevel(3);
            history.push('/b/' + item.buildingId);
        } else {
            if (item.type === 'DONG') {
                setLevel(5);
            } else {
                setLevel(8);
            }
            history.push('/r/' + item.id);
        }
    };

    return (
        <>
            <div className={cx('header_container')}>
                <div className={cx('title_container')}>
                    <WingBlank>
                        <div className={cx('left')}
                             onClick={() => history.push(location.pathname.replace('/search', ''))}>
                            {/*<SearchOutlined/>*/}
                            <ArrowLeftOutlined/>
                        </div>
                    </WingBlank>
                    <div className={cx('center')}>
                        {/*<span className={cx('title')}>{region.address}</span>*/}
                        <input ref={queryInput}
                               value={query}
                               onKeyPress={(e) => {
                                   if (e.key === 'Enter') {
                                       fetchRegion();
                                   }
                               }}
                               onChange={onChangeQuery}
                               className={cx('search_input')}
                               placeholder="지역, 아파트명을 입력하세요">
                        </input>
                    </div>
                    <WingBlank>
                        {/*<div className={cx('right')} onClick={() => history.push(location.pathname.replace('/search', ''))}>*/}
                        <div className={cx('right')}>
                            <CloseOutlined style={{visibility: 'hidden'}}/>
                        </div>
                    </WingBlank>
                </div>
            </div>
            <div className={cx('query_container')}>
                <List>
                    {
                        (!loading && queryList.length <= 0) &&
                        <List.Item>결과없음</List.Item>
                    }
                    {
                        queryList.map(x => {
                                if (x.type === 'BUILDING') {
                                    return (
                                        <List.Item
                                            onClick={() => handleResultItem(x)}>
                                            <ReconciliationOutlined/>
                                            <Highlighter
                                                style={{marginLeft: 4}}
                                                highlightClassName={cx('highlight')}
                                                searchWords={query.split('')}
                                                autoEscape={true}
                                                textToHighlight={x.name}
                                            /><List.Item.Brief
                                                style={{
                                                    fontSize: '0.7rem',
                                                    marginTop: 2,
                                                    marginBottom: 4
                                                }}>
                                                <Highlighter
                                                    highlightClassName={cx('highlight')}
                                                    searchWords={query.split('')}
                                                    autoEscape={true}
                                                    textToHighlight={x.address}
                                                />
                                            </List.Item.Brief></List.Item>
                                    )
                                } else {
                                    return (
                                        <List.Item onClick={() => handleResultItem(x)}>
                                            <EnvironmentOutlined/>
                                            <Highlighter
                                                style={{marginLeft: 4}}
                                                highlightClassName={cx('highlight')}
                                                searchWords={query.split('')}
                                                autoEscape={true}
                                                textToHighlight={x.address}
                                            />
                                        </List.Item>
                                    )
                                }
                            }
                        )
                    }
                </List>
            </div>
        </>
    )
};

export default GuavaSearchHeader;