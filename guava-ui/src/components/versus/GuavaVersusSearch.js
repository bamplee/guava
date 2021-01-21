import React, {useEffect, useRef, useState} from 'react'

import {useHistory, useLocation} from 'react-router-dom';

import classNames from 'classnames/bind';

import styles from './guavaVersusSearch.module.scss';
import {useRecoilState} from 'recoil';
import {centerState, levelState, regionState, showVersusSearchState, versusRegionListState} from '../datatool/state';
import ArrowLeftOutlined from '@ant-design/icons/es/icons/ArrowLeftOutlined';
import {Icon, List, Modal, Result, WingBlank} from 'antd-mobile/lib/index';
import {fetchSearch} from '../datatool/api';
import CloseOutlined from '@ant-design/icons/es/icons/CloseOutlined';
import ReconciliationOutlined from '@ant-design/icons/es/icons/ReconciliationOutlined';
import EnvironmentOutlined from '@ant-design/icons/es/icons/EnvironmentOutlined';
import Highlighter from 'react-highlight-words';
// import {useLocalStorage} from '../common/useLocalStorage';
import queryString from 'query-string/index';
import SearchOutlined from '@ant-design/icons/es/icons/SearchOutlined';

const cx = classNames.bind(styles);

const GuavaVersusSearch = () => {
    // const location = useLocation();
    // const history = useHistory();
    // const queryInput = useRef(null);
    const [query, setQuery] = useState('');
    const [queryList, setQueryList] = useState([]);
    const [loading, setLoading] = useState(false);
    // const [level, setLevel] = useRecoilState(levelState);
    const [region, setRegion] = useRecoilState(regionState);
    // const [searchList, setSearchList] = useLocalStorage('searchList', []);
    // const [center, setCenter] = useRecoilState(centerState);
    const [showVersusSearch, setShowVersusSearch] = useRecoilState(showVersusSearchState);
    const [versusRegionList, setVersusRegionList] = useRecoilState(versusRegionListState);

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
                regionList = regionList.filter(x => x.type === region.type);
                setQueryList(regionList);
                setLoading(false);
            }
        }
    };

    const handleResultItem = async (item) => {
        let vsList = [...versusRegionList];
        vsList.unshift(item);
        setVersusRegionList([...vsList]);
        setShowVersusSearch(false);
        setQuery('');
        setQueryList([]);
        // let temp = searchList.filter(x => !(x.type === 'BUILDING' ? x.buildingId === item.buildingId : x.id === item.id));
        // temp.unshift(item);
        // if (temp.length > 10) {
        //     temp = temp.subList(0, 10);
        // }
        // setSearchList(temp);
    };

    const removeSearchList = (item) => {
        // let temp = searchList.filter(x => !(x.type === 'BUILDING' ? x.buildingId === item.buildingId : x.id === item.id));
        // setSearchList(temp);
    };

    return (
        <Modal
            popup
            visible={showVersusSearch}
            onCancel={() => setShowVersusSearch(false)}
            onClose={() => setShowVersusSearch(false)}
            animationType="slide-up"
        >
            <div className={cx('header_container')}>
                <div className={cx('title_container')}>
                    <WingBlank>
                        <div className={cx('left')} onClick={() => {
                            setShowVersusSearch(false)
                        }}>
                            {/*<SearchOutlined/>*/}
                            {/*<ArrowLeftOutlined style={{visibility: 'hidden'}}/>*/}
                            <CloseOutlined/>
                        </div>
                    </WingBlank>
                    <div className={cx('center')}>
                        {/*<span className={cx('title')}>{region.address}</span>*/}
                        <input autoFocus={true} ref={(ref) => {
                            if (ref !== null) {
                                ref.focus();
                            }
                        }}
                               value={query}
                               onKeyPress={(e) => {
                                   if (e.key === 'Enter') {
                                       fetchRegion();
                                   }
                               }}
                               onChange={onChangeQuery}
                               placeholder="지역 / 아파트 명을 입력하세요">
                        </input>
                    </div>
                    <WingBlank>
                        {/*<div className={cx('right')} onClick={() => history.push(location.pathname.replace('/search', ''))}>*/}
                        <div className={cx('right')}>
                            <SearchOutlined/>
                        </div>
                    </WingBlank>
                </div>
            </div>
            <div className={cx('query_container')}>
                {
                    (!loading && query.length === 0) &&
                    <>
                        {
                            <List.Item>
                                <span className={cx('search_list_title')}>주변 지역/아파트</span>
                            </List.Item>
                        }
                        {
                            [].map(x =>
                                <List.Item>
                                    <div className={cx('search_list')}>
                                        <div className={cx('left')} onClick={() => handleResultItem(x)}>
                                            {
                                                x.type === 'BUILDING' ?
                                                    <ReconciliationOutlined/> :
                                                    <EnvironmentOutlined/>
                                            }
                                            <Highlighter
                                                style={{marginLeft: 4}}
                                                highlightClassName={cx('highlight')}
                                                searchWords={query.split('')}
                                                autoEscape={true}
                                                textToHighlight={x.name}
                                            />
                                            <List.Item.Brief style={{
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
                                            </List.Item.Brief>
                                        </div>
                                        <div className={cx('right')} onClick={() => removeSearchList(x)}>
                                            <CloseOutlined style={{fontSize: 12}}/>
                                        </div>
                                    </div>
                                </List.Item>
                            )
                        }
                    </>
                }
                <List>
                    {
                        query.length > 0 && queryList.length === 0 &&
                        <Result
                            img={<img
                                src={'https://gw.alipayobjects.com/zos/rmsportal/GIyMDJnuqmcqPLpHCSkj.svg'}
                                style={{width: 40, height: 40}}
                                alt=""/>}
                            message="검색 결과가 없습니다"
                        />
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
        </Modal>
    )
};

export default GuavaVersusSearch;